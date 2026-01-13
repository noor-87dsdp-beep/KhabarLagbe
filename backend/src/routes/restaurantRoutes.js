const express = require('express');
const router = express.Router();
const restaurantController = require('../controllers/restaurantController');
const { auth } = require('../middleware/auth');

// Public routes
router.get('/', restaurantController.getRestaurants);
router.get('/featured', restaurantController.getFeaturedRestaurants);
router.get('/search', restaurantController.searchAll);
router.get('/:id', restaurantController.getRestaurantById);

module.exports = router;
