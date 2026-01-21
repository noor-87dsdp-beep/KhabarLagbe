const express = require('express');
const router = express.Router();
const reviewController = require('../controllers/reviewController');
const { auth, restaurantAuth, adminAuth } = require('../middleware/auth');
const { body } = require('express-validator');
const validate = require('../middleware/validator');

// Public routes
router.get('/restaurant/:restaurantId', reviewController.getRestaurantReviews);

// User routes (requires authentication)
router.post(
  '/',
  auth,
  [
    body('orderId').notEmpty().withMessage('Order ID is required'),
    body('foodRating').isInt({ min: 1, max: 5 }).withMessage('Food rating must be between 1 and 5'),
    body('deliveryRating').optional().isInt({ min: 1, max: 5 }).withMessage('Delivery rating must be between 1 and 5'),
  ],
  validate,
  reviewController.createReview
);

router.get('/my-reviews', auth, reviewController.getUserReviews);
router.put('/:reviewId', auth, reviewController.updateReview);
router.delete('/:reviewId', auth, reviewController.deleteReview);

// Restaurant owner routes
router.post('/:reviewId/respond', restaurantAuth, reviewController.respondToReview);

// Admin routes
router.get('/', adminAuth, reviewController.getAllReviews);
router.patch('/:reviewId/toggle-visibility', adminAuth, reviewController.toggleReviewVisibility);

module.exports = router;
