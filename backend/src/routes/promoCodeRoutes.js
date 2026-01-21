const express = require('express');
const router = express.Router();
const promoCodeController = require('../controllers/promoCodeController');
const { auth, adminAuth } = require('../middleware/auth');
const { body } = require('express-validator');
const validate = require('../middleware/validator');

// User routes
router.get('/active', auth, promoCodeController.getActivePromoCodes);
router.post(
  '/validate',
  auth,
  [
    body('code').notEmpty().withMessage('Promo code is required'),
    body('orderAmount').isNumeric().withMessage('Order amount must be a number'),
  ],
  validate,
  promoCodeController.validatePromoCode
);

// Admin routes
router.post(
  '/',
  adminAuth,
  [
    body('code').notEmpty().withMessage('Code is required'),
    body('type').isIn(['percentage', 'fixed']).withMessage('Invalid type'),
    body('value').isNumeric().withMessage('Value must be a number'),
    body('validFrom').isISO8601().withMessage('Valid from date is required'),
    body('validUntil').isISO8601().withMessage('Valid until date is required'),
  ],
  validate,
  promoCodeController.createPromoCode
);

router.get('/', adminAuth, promoCodeController.getAllPromoCodes);
router.get('/:promoId', adminAuth, promoCodeController.getPromoCodeById);
router.get('/:promoId/stats', adminAuth, promoCodeController.getPromoCodeStats);
router.put('/:promoId', adminAuth, promoCodeController.updatePromoCode);
router.patch('/:promoId/toggle-status', adminAuth, promoCodeController.togglePromoCodeStatus);
router.delete('/:promoId', adminAuth, promoCodeController.deletePromoCode);

module.exports = router;
