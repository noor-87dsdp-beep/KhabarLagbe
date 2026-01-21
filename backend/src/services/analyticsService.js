const Order = require('../models/Order');
const User = require('../models/User');
const Restaurant = require('../models/Restaurant');
const Rider = require('../models/Rider');
const Payment = require('../models/Payment');
const Review = require('../models/Review');
const mongoose = require('mongoose');

class AnalyticsService {
  // Get date range based on period
  getDateRange(period) {
    const now = new Date();
    const start = new Date();

    switch (period) {
      case 'today':
        start.setHours(0, 0, 0, 0);
        break;
      case 'yesterday':
        start.setDate(start.getDate() - 1);
        start.setHours(0, 0, 0, 0);
        now.setDate(now.getDate() - 1);
        now.setHours(23, 59, 59, 999);
        break;
      case 'week':
        start.setDate(start.getDate() - 7);
        break;
      case 'month':
        start.setMonth(start.getMonth() - 1);
        break;
      case 'quarter':
        start.setMonth(start.getMonth() - 3);
        break;
      case 'year':
        start.setFullYear(start.getFullYear() - 1);
        break;
      default:
        start.setDate(start.getDate() - 30); // Default 30 days
    }

    return { start, end: now };
  }

  // Dashboard summary stats
  async getDashboardStats(period = 'today') {
    const { start, end } = this.getDateRange(period);
    const dateFilter = { createdAt: { $gte: start, $lte: end } };

    const [
      orderStats,
      revenueStats,
      userCount,
      restaurantCount,
      riderCount,
      activeOrderCount,
    ] = await Promise.all([
      // Order stats
      Order.aggregate([
        { $match: dateFilter },
        {
          $group: {
            _id: null,
            total: { $sum: 1 },
            delivered: { $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] } },
            cancelled: { $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] } },
          },
        },
      ]),

      // Revenue stats
      Order.aggregate([
        { $match: { ...dateFilter, status: { $ne: 'cancelled' } } },
        {
          $group: {
            _id: null,
            totalRevenue: { $sum: '$total' },
            avgOrderValue: { $avg: '$total' },
            totalDeliveryFees: { $sum: '$deliveryFee' },
            totalDiscounts: { $sum: '$discount' },
          },
        },
      ]),

      // User count
      User.countDocuments({ ...dateFilter, role: 'customer' }),

      // Restaurant count
      Restaurant.countDocuments({ isActive: true }),

      // Rider count
      Rider.countDocuments({ isActive: true }),

      // Active orders
      Order.countDocuments({
        status: { $in: ['pending', 'confirmed', 'preparing', 'ready', 'picked_up', 'on_the_way'] },
      }),
    ]);

    return {
      orders: {
        total: orderStats[0]?.total || 0,
        delivered: orderStats[0]?.delivered || 0,
        cancelled: orderStats[0]?.cancelled || 0,
        completionRate: orderStats[0]?.total
          ? ((orderStats[0].delivered / orderStats[0].total) * 100).toFixed(1)
          : 0,
      },
      revenue: {
        total: revenueStats[0]?.totalRevenue || 0,
        avgOrderValue: Math.round(revenueStats[0]?.avgOrderValue || 0),
        deliveryFees: revenueStats[0]?.totalDeliveryFees || 0,
        discounts: revenueStats[0]?.totalDiscounts || 0,
      },
      users: {
        newCustomers: userCount,
        totalRestaurants: restaurantCount,
        totalRiders: riderCount,
      },
      realtime: {
        activeOrders: activeOrderCount,
      },
      period,
    };
  }

  // Order trends over time
  async getOrderTrends(startDate, endDate, groupBy = 'day') {
    let dateFormat;
    switch (groupBy) {
      case 'hour':
        dateFormat = { $hour: '$createdAt' };
        break;
      case 'day':
        dateFormat = { $dateToString: { format: '%Y-%m-%d', date: '$createdAt' } };
        break;
      case 'week':
        dateFormat = { $isoWeek: '$createdAt' };
        break;
      case 'month':
        dateFormat = { $dateToString: { format: '%Y-%m', date: '$createdAt' } };
        break;
      default:
        dateFormat = { $dateToString: { format: '%Y-%m-%d', date: '$createdAt' } };
    }

    const trends = await Order.aggregate([
      {
        $match: {
          createdAt: {
            $gte: new Date(startDate),
            $lte: new Date(endDate),
          },
        },
      },
      {
        $group: {
          _id: dateFormat,
          orders: { $sum: 1 },
          revenue: { $sum: '$total' },
          delivered: { $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] } },
          cancelled: { $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] } },
        },
      },
      { $sort: { _id: 1 } },
    ]);

    return trends;
  }

  // Top performing restaurants
  async getTopRestaurants(limit = 10, period = 'month') {
    const { start, end } = this.getDateRange(period);

    const topRestaurants = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: { $ne: 'cancelled' },
        },
      },
      {
        $group: {
          _id: '$restaurant',
          orders: { $sum: 1 },
          revenue: { $sum: '$total' },
        },
      },
      { $sort: { orders: -1 } },
      { $limit: limit },
      {
        $lookup: {
          from: 'restaurants',
          localField: '_id',
          foreignField: '_id',
          as: 'restaurant',
        },
      },
      { $unwind: '$restaurant' },
      {
        $project: {
          name: '$restaurant.name',
          logo: '$restaurant.logo',
          rating: '$restaurant.rating',
          orders: 1,
          revenue: 1,
        },
      },
    ]);

    return topRestaurants;
  }

  // Top riders by deliveries
  async getTopRiders(limit = 10, period = 'month') {
    const { start, end } = this.getDateRange(period);

    const topRiders = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: 'delivered',
          rider: { $exists: true },
        },
      },
      {
        $group: {
          _id: '$rider',
          deliveries: { $sum: 1 },
          earnings: { $sum: '$deliveryFee' },
        },
      },
      { $sort: { deliveries: -1 } },
      { $limit: limit },
      {
        $lookup: {
          from: 'riders',
          localField: '_id',
          foreignField: '_id',
          as: 'rider',
        },
      },
      { $unwind: '$rider' },
      {
        $project: {
          name: '$rider.name',
          phone: '$rider.phone',
          rating: '$rider.rating',
          deliveries: 1,
          earnings: 1,
        },
      },
    ]);

    return topRiders;
  }

  // Popular menu items
  async getPopularItems(limit = 20, period = 'month') {
    const { start, end } = this.getDateRange(period);

    const popularItems = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: { $ne: 'cancelled' },
        },
      },
      { $unwind: '$items' },
      {
        $group: {
          _id: '$items.menuItem',
          name: { $first: '$items.name' },
          orders: { $sum: '$items.quantity' },
          revenue: { $sum: { $multiply: ['$items.price', '$items.quantity'] } },
        },
      },
      { $sort: { orders: -1 } },
      { $limit: limit },
    ]);

    return popularItems;
  }

  // Payment method distribution
  async getPaymentDistribution(period = 'month') {
    const { start, end } = this.getDateRange(period);

    const distribution = await Payment.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: 'success',
        },
      },
      {
        $group: {
          _id: '$method',
          count: { $sum: 1 },
          amount: { $sum: '$amount' },
        },
      },
      { $sort: { count: -1 } },
    ]);

    return distribution;
  }

  // Customer retention analysis
  async getCustomerRetention(period = 'month') {
    const { start, end } = this.getDateRange(period);

    const retention = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: 'delivered',
        },
      },
      {
        $group: {
          _id: '$user',
          orderCount: { $sum: 1 },
        },
      },
      {
        $group: {
          _id: {
            $switch: {
              branches: [
                { case: { $eq: ['$orderCount', 1] }, then: 'new' },
                { case: { $lte: ['$orderCount', 3] }, then: 'returning' },
                { case: { $lte: ['$orderCount', 10] }, then: 'regular' },
              ],
              default: 'loyal',
            },
          },
          count: { $sum: 1 },
        },
      },
    ]);

    return retention;
  }

  // Peak hours analysis
  async getPeakHours(period = 'month') {
    const { start, end } = this.getDateRange(period);

    const peakHours = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
        },
      },
      {
        $group: {
          _id: { $hour: '$createdAt' },
          orders: { $sum: 1 },
          revenue: { $sum: '$total' },
        },
      },
      { $sort: { _id: 1 } },
    ]);

    return peakHours;
  }

  // Zone performance
  async getZonePerformance(period = 'month') {
    const { start, end } = this.getDateRange(period);

    const zoneStats = await Order.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
        },
      },
      {
        $group: {
          _id: '$deliveryAddress.thana',
          orders: { $sum: 1 },
          revenue: { $sum: '$total' },
          avgDeliveryTime: { $avg: '$deliveryTime' },
        },
      },
      { $sort: { orders: -1 } },
      { $limit: 20 },
    ]);

    return zoneStats;
  }

  // Restaurant earnings report
  async getRestaurantEarningsReport(restaurantId, startDate, endDate) {
    const earnings = await Order.aggregate([
      {
        $match: {
          restaurant: new mongoose.Types.ObjectId(restaurantId),
          createdAt: { $gte: new Date(startDate), $lte: new Date(endDate) },
          status: 'delivered',
        },
      },
      {
        $group: {
          _id: { $dateToString: { format: '%Y-%m-%d', date: '$createdAt' } },
          orders: { $sum: 1 },
          grossRevenue: { $sum: '$subtotal' },
          commission: { $sum: { $multiply: ['$subtotal', 0.18] } }, // 18% commission
        },
      },
      { $sort: { _id: 1 } },
    ]);

    const totals = earnings.reduce(
      (acc, day) => ({
        totalOrders: acc.totalOrders + day.orders,
        totalGross: acc.totalGross + day.grossRevenue,
        totalCommission: acc.totalCommission + day.commission,
      }),
      { totalOrders: 0, totalGross: 0, totalCommission: 0 }
    );

    return {
      daily: earnings,
      totals: {
        ...totals,
        netEarnings: totals.totalGross - totals.totalCommission,
      },
    };
  }
}

module.exports = new AnalyticsService();
