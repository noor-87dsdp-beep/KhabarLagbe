const express = require('express');
const router = express.Router();
const riderController = require('../controllers/riderController');
const { riderAuth, adminAuth } = require('../middleware/auth');
const { body } = require('express-validator');
const validate = require('../middleware/validator');

// Public routes
router.post(
  '/register',
  [
    body('name').notEmpty().withMessage('Name is required'),
    body('phone').matches(/^\+880\d{10}$/).withMessage('Invalid Bangladesh phone number'),
    body('vehicleType').isIn(['motorcycle', 'bicycle', 'car']).withMessage('Invalid vehicle type'),
  ],
  validate,
  riderController.registerRider
);

// Rider routes (requires rider authentication)
router.get('/profile', riderAuth, riderController.getRiderProfile);
router.put('/profile', riderAuth, riderController.updateRiderProfile);
router.patch('/status', riderAuth, riderController.updateStatus);
router.patch('/location', riderAuth, riderController.updateLocation);

router.get('/available-orders', riderAuth, riderController.getAvailableOrders);
router.post('/orders/:orderId/accept', riderAuth, riderController.acceptOrder);
router.patch('/orders/:orderId/status', riderAuth, riderController.updateOrderStatus);

router.get('/earnings', riderAuth, riderController.getEarnings);
router.get('/delivery-history', riderAuth, riderController.getDeliveryHistory);
router.get('/stats', riderAuth, riderController.getRiderStats);

// Admin routes
router.get('/', adminAuth, riderController.getAllRiders);
router.patch('/:riderId/approve', adminAuth, riderController.approveRider);
router.patch('/:riderId/suspend', adminAuth, riderController.suspendRider);

module.exports = router;
