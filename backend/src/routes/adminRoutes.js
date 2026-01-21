const express = require('express');
const router = express.Router();
const adminController = require('../controllers/adminController');
const { adminAuth } = require('../middleware/auth');

// All admin routes require admin authorization
router.get('/dashboard', adminAuth, adminController.getDashboardStats);
router.get('/activities', adminAuth, adminController.getRecentActivities);
router.get('/analytics', adminAuth, adminController.getAnalytics);
router.get('/revenue', adminAuth, adminController.getRevenueAnalytics);
router.get('/user-growth', adminAuth, adminController.getUserGrowthAnalytics);
router.get('/export', adminAuth, adminController.exportData);

module.exports = router;
