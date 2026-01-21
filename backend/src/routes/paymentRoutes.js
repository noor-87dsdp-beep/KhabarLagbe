const express = require('express');
const router = express.Router();
const paymentController = require('../controllers/paymentController');
const { auth, adminAuth } = require('../middleware/auth');
const { body } = require('express-validator');
const validate = require('../middleware/validator');

// Callback routes (public - called by payment gateways)
router.post('/bkash/callback', paymentController.bkashCallback);
router.post('/nagad/callback', paymentController.nagadCallback);
router.post('/sslcommerz/callback', paymentController.sslCommerzCallback);

// User routes (requires authentication)
router.post(
  '/create',
  auth,
  [
    body('orderId').notEmpty().withMessage('Order ID is required'),
    body('method').isIn(['bkash', 'nagad', 'rocket', 'card', 'cod']).withMessage('Invalid payment method'),
  ],
  validate,
  paymentController.createPayment
);

router.get('/order/:orderId', auth, paymentController.getPaymentByOrderId);
router.get('/history', auth, paymentController.getPaymentHistory);

// Admin routes
router.get('/', adminAuth, paymentController.getAllPayments);
router.post('/:paymentId/refund', adminAuth, paymentController.refundPayment);

module.exports = router;
