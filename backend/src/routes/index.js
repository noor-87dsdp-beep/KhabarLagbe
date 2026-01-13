const express = require('express');
const router = express.Router();

// Import route modules
const authRoutes = require('./authRoutes');
const userRoutes = require('./userRoutes');
const restaurantRoutes = require('./restaurantRoutes');
const orderRoutes = require('./orderRoutes');
const zoneRoutes = require('./zoneRoutes');
const categoryRoutes = require('./categoryRoutes');
const menuItemRoutes = require('./menuItemRoutes');

// Mount routes
router.use('/auth', authRoutes);
router.use('/users', userRoutes);
router.use('/restaurants', restaurantRoutes);
router.use('/orders', orderRoutes);
router.use('/zones', zoneRoutes);
router.use('/categories', categoryRoutes);
router.use('/menu-items', menuItemRoutes);

// API info endpoint
router.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'KhabarLagbe API v1',
    version: '1.0.0',
    endpoints: {
      auth: '/api/v1/auth',
      users: '/api/v1/users',
      restaurants: '/api/v1/restaurants',
      orders: '/api/v1/orders',
      zones: '/api/v1/zones',
      categories: '/api/v1/categories',
      menuItems: '/api/v1/menu-items',
    },
    documentation: 'https://api.khabarlagbe.com/docs',
  });
});

module.exports = router;
