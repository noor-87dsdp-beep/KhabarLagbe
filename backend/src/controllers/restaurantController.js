const Restaurant = require('../models/Restaurant');
const MenuItem = require('../models/MenuItem');

// Get restaurants (with filters and pagination)
exports.getRestaurants = async (req, res, next) => {
  try {
    const { 
      lat, 
      lng, 
      radius = 10, // km
      cuisine, 
      search, 
      minRating,
      page = 1,
      limit = 20,
      sortBy = 'rating' // rating, deliveryTime, distance
    } = req.query;

    const query = { isActive: true };

    // Search by name or cuisine
    if (search) {
      query.$or = [
        { name: { $regex: search, $options: 'i' } },
        { nameBn: { $regex: search, $options: 'i' } },
        { cuisines: { $regex: search, $options: 'i' } },
      ];
    }

    // Filter by cuisine
    if (cuisine) {
      query.cuisines = cuisine;
    }

    // Filter by minimum rating
    if (minRating) {
      query.rating = { $gte: parseFloat(minRating) };
    }

    // Geospatial query if location provided
    if (lat && lng) {
      query.location = {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [parseFloat(lng), parseFloat(lat)],
          },
          $maxDistance: parseInt(radius) * 1000, // Convert km to meters
        },
      };
    }

    let sortOptions = {};
    if (sortBy === 'rating') sortOptions = { rating: -1, totalReviews: -1 };
    if (sortBy === 'deliveryTime') sortOptions = { 'deliveryTime.min': 1 };

    const restaurants = await Restaurant.find(query)
      .sort(sortOptions)
      .limit(parseInt(limit))
      .skip((parseInt(page) - 1) * parseInt(limit))
      .select('-__v');

    const total = await Restaurant.countDocuments(query);

    res.status(200).json({
      success: true,
      data: {
        restaurants,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / parseInt(limit)),
        },
      },
    });
  } catch (error) {
    next(error);
  }
};

// Get restaurant by ID with menu
exports.getRestaurantById = async (req, res, next) => {
  try {
    const { id } = req.params;

    const restaurant = await Restaurant.findById(id).select('-__v');

    if (!restaurant) {
      return res.status(404).json({
        success: false,
        message: 'Restaurant not found',
      });
    }

    // Get menu items
    const menuItems = await MenuItem.find({ 
      restaurant: id, 
      isAvailable: true 
    }).select('-__v');

    // Group menu items by category
    const menuByCategory = menuItems.reduce((acc, item) => {
      if (!acc[item.category]) {
        acc[item.category] = [];
      }
      acc[item.category].push(item);
      return acc;
    }, {});

    res.status(200).json({
      success: true,
      data: {
        restaurant,
        menu: menuByCategory,
      },
    });
  } catch (error) {
    next(error);
  }
};

// Get featured restaurants
exports.getFeaturedRestaurants = async (req, res, next) => {
  try {
    const restaurants = await Restaurant.find({ 
      isActive: true, 
      featured: true 
    })
      .sort({ rating: -1 })
      .limit(10)
      .select('-__v');

    res.status(200).json({
      success: true,
      data: { restaurants },
    });
  } catch (error) {
    next(error);
  }
};

// Search restaurants and menu items
exports.searchAll = async (req, res, next) => {
  try {
    const { query: searchQuery } = req.query;

    if (!searchQuery) {
      return res.status(400).json({
        success: false,
        message: 'Search query is required',
      });
    }

    // Search restaurants
    const restaurants = await Restaurant.find({
      isActive: true,
      $or: [
        { name: { $regex: searchQuery, $options: 'i' } },
        { nameBn: { $regex: searchQuery, $options: 'i' } },
        { cuisines: { $regex: searchQuery, $options: 'i' } },
      ],
    }).limit(10);

    // Search menu items
    const menuItems = await MenuItem.find({
      isAvailable: true,
      $or: [
        { name: { $regex: searchQuery, $options: 'i' } },
        { nameBn: { $regex: searchQuery, $options: 'i' } },
        { description: { $regex: searchQuery, $options: 'i' } },
        { category: { $regex: searchQuery, $options: 'i' } },
      ],
    })
      .populate('restaurant', 'name logo rating')
      .limit(20);

    res.status(200).json({
      success: true,
      data: {
        restaurants,
        menuItems,
      },
    });
  } catch (error) {
    next(error);
  }
};
