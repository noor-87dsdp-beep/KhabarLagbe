const { body, param, query } = require('express-validator');

// Bangladesh phone validation
const phoneValidation = body('phone')
  .matches(/^\+880\d{10}$/)
  .withMessage('Invalid Bangladesh phone number. Format: +880XXXXXXXXXX');

// OTP validation
const otpValidation = body('otp')
  .isLength({ min: 6, max: 6 })
  .withMessage('OTP must be 6 digits')
  .isNumeric()
  .withMessage('OTP must contain only numbers');

// User validators
const userValidators = {
  updateProfile: [
    body('name')
      .optional()
      .isLength({ min: 2, max: 100 })
      .withMessage('Name must be between 2 and 100 characters'),
    body('email')
      .optional()
      .isEmail()
      .withMessage('Invalid email address'),
    body('preferredLanguage')
      .optional()
      .isIn(['en', 'bn'])
      .withMessage('Language must be en or bn'),
  ],

  addAddress: [
    body('coordinates')
      .isArray({ min: 2, max: 2 })
      .withMessage('Coordinates must be [longitude, latitude]'),
    body('coordinates.*')
      .isFloat()
      .withMessage('Coordinates must be numbers'),
    body('area')
      .notEmpty()
      .withMessage('Area is required'),
    body('label')
      .optional()
      .isIn(['Home', 'Office', 'Other'])
      .withMessage('Label must be Home, Office, or Other'),
  ],
};

// Restaurant validators
const restaurantValidators = {
  create: [
    body('name')
      .notEmpty()
      .withMessage('Restaurant name is required')
      .isLength({ min: 2, max: 200 })
      .withMessage('Name must be between 2 and 200 characters'),
    body('phone')
      .notEmpty()
      .withMessage('Phone is required')
      .matches(/^\+880\d{10}$/)
      .withMessage('Invalid Bangladesh phone number'),
    body('location.coordinates')
      .isArray({ min: 2, max: 2 })
      .withMessage('Location coordinates required'),
    body('address.area')
      .notEmpty()
      .withMessage('Area is required'),
  ],

  update: [
    body('name')
      .optional()
      .isLength({ min: 2, max: 200 })
      .withMessage('Name must be between 2 and 200 characters'),
    body('minOrderAmount')
      .optional()
      .isInt({ min: 0 })
      .withMessage('Minimum order amount must be a positive number'),
    body('deliveryRadius')
      .optional()
      .isFloat({ min: 0.5, max: 50 })
      .withMessage('Delivery radius must be between 0.5 and 50 km'),
  ],
};

// Menu item validators
const menuItemValidators = {
  create: [
    body('name')
      .notEmpty()
      .withMessage('Item name is required')
      .isLength({ min: 2, max: 200 })
      .withMessage('Name must be between 2 and 200 characters'),
    body('category')
      .notEmpty()
      .withMessage('Category is required')
      .isMongoId()
      .withMessage('Invalid category ID'),
    body('price')
      .notEmpty()
      .withMessage('Price is required')
      .isInt({ min: 100 })
      .withMessage('Price must be at least ৳1 (100 paisa)'),
    body('discountPrice')
      .optional()
      .isInt({ min: 0 })
      .withMessage('Discount price must be a positive number')
      .custom((value, { req }) => {
        if (value >= req.body.price) {
          throw new Error('Discount price must be less than regular price');
        }
        return true;
      }),
    body('spiceLevel')
      .optional()
      .isIn(['None', 'Mild', 'Medium', 'Hot', 'Extra Hot'])
      .withMessage('Invalid spice level'),
  ],

  update: [
    body('name')
      .optional()
      .isLength({ min: 2, max: 200 })
      .withMessage('Name must be between 2 and 200 characters'),
    body('price')
      .optional()
      .isInt({ min: 100 })
      .withMessage('Price must be at least ৳1 (100 paisa)'),
  ],
};

// Order validators
const orderValidators = {
  create: [
    body('restaurantId')
      .notEmpty()
      .withMessage('Restaurant ID is required')
      .isMongoId()
      .withMessage('Invalid restaurant ID'),
    body('items')
      .isArray({ min: 1 })
      .withMessage('Order must have at least one item'),
    body('items.*.menuItemId')
      .notEmpty()
      .withMessage('Menu item ID is required')
      .isMongoId()
      .withMessage('Invalid menu item ID'),
    body('items.*.quantity')
      .isInt({ min: 1, max: 50 })
      .withMessage('Quantity must be between 1 and 50'),
    body('deliveryAddress')
      .notEmpty()
      .withMessage('Delivery address is required'),
    body('deliveryAddress.coordinates')
      .isArray({ min: 2, max: 2 })
      .withMessage('Delivery coordinates required'),
    body('paymentMethod')
      .isIn(['bkash', 'nagad', 'rocket', 'card', 'cod'])
      .withMessage('Invalid payment method'),
  ],

  updateStatus: [
    body('status')
      .notEmpty()
      .withMessage('Status is required')
      .isIn(['pending', 'confirmed', 'preparing', 'ready', 'picked_up', 'on_the_way', 'delivered', 'cancelled'])
      .withMessage('Invalid order status'),
  ],

  cancel: [
    body('reason')
      .notEmpty()
      .withMessage('Cancellation reason is required')
      .isLength({ min: 5, max: 500 })
      .withMessage('Reason must be between 5 and 500 characters'),
  ],
};

// Review validators
const reviewValidators = {
  create: [
    body('orderId')
      .notEmpty()
      .withMessage('Order ID is required')
      .isMongoId()
      .withMessage('Invalid order ID'),
    body('foodRating')
      .isInt({ min: 1, max: 5 })
      .withMessage('Food rating must be between 1 and 5'),
    body('deliveryRating')
      .optional()
      .isInt({ min: 1, max: 5 })
      .withMessage('Delivery rating must be between 1 and 5'),
    body('review')
      .optional()
      .isLength({ max: 1000 })
      .withMessage('Review must not exceed 1000 characters'),
  ],
};

// Promo code validators
const promoCodeValidators = {
  create: [
    body('code')
      .notEmpty()
      .withMessage('Code is required')
      .isLength({ min: 3, max: 20 })
      .withMessage('Code must be between 3 and 20 characters')
      .matches(/^[A-Z0-9]+$/)
      .withMessage('Code must contain only uppercase letters and numbers'),
    body('type')
      .isIn(['percentage', 'fixed'])
      .withMessage('Type must be percentage or fixed'),
    body('value')
      .isInt({ min: 1 })
      .withMessage('Value must be a positive number'),
    body('validFrom')
      .isISO8601()
      .withMessage('Valid from must be a valid date'),
    body('validUntil')
      .isISO8601()
      .withMessage('Valid until must be a valid date')
      .custom((value, { req }) => {
        if (new Date(value) <= new Date(req.body.validFrom)) {
          throw new Error('Valid until must be after valid from');
        }
        return true;
      }),
  ],

  validate: [
    body('code')
      .notEmpty()
      .withMessage('Promo code is required'),
    body('orderAmount')
      .isInt({ min: 0 })
      .withMessage('Order amount must be a positive number'),
  ],
};

// Payment validators
const paymentValidators = {
  create: [
    body('orderId')
      .notEmpty()
      .withMessage('Order ID is required')
      .isMongoId()
      .withMessage('Invalid order ID'),
    body('method')
      .isIn(['bkash', 'nagad', 'rocket', 'card', 'cod'])
      .withMessage('Invalid payment method'),
  ],

  refund: [
    body('reason')
      .notEmpty()
      .withMessage('Refund reason is required'),
    body('amount')
      .optional()
      .isInt({ min: 100 })
      .withMessage('Refund amount must be at least ৳1'),
  ],
};

// Zone validators
const zoneValidators = {
  create: [
    body('name')
      .notEmpty()
      .withMessage('Zone name is required'),
    body('nameBn')
      .notEmpty()
      .withMessage('Zone name in Bangla is required'),
    body('polygon')
      .notEmpty()
      .withMessage('Polygon coordinates are required'),
    body('polygon.coordinates')
      .isArray()
      .withMessage('Polygon coordinates must be an array'),
    body('center')
      .notEmpty()
      .withMessage('Center point is required'),
    body('center.coordinates')
      .isArray({ min: 2, max: 2 })
      .withMessage('Center must have longitude and latitude'),
    body('deliveryFee')
      .optional()
      .isInt({ min: 0 })
      .withMessage('Delivery fee must be a positive number'),
  ],
};

// Rider validators
const riderValidators = {
  register: [
    body('name')
      .notEmpty()
      .withMessage('Name is required'),
    body('phone')
      .matches(/^\+880\d{10}$/)
      .withMessage('Invalid Bangladesh phone number'),
    body('vehicleType')
      .isIn(['motorcycle', 'bicycle', 'car'])
      .withMessage('Invalid vehicle type'),
    body('nid')
      .optional()
      .isLength({ min: 10, max: 17 })
      .withMessage('Invalid NID number'),
  ],

  updateLocation: [
    body('latitude')
      .isFloat({ min: -90, max: 90 })
      .withMessage('Invalid latitude'),
    body('longitude')
      .isFloat({ min: -180, max: 180 })
      .withMessage('Invalid longitude'),
  ],
};

// Common validators
const mongoIdParam = param('id')
  .isMongoId()
  .withMessage('Invalid ID format');

const paginationQuery = [
  query('page')
    .optional()
    .isInt({ min: 1 })
    .withMessage('Page must be a positive integer'),
  query('limit')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Limit must be between 1 and 100'),
];

module.exports = {
  phoneValidation,
  otpValidation,
  userValidators,
  restaurantValidators,
  menuItemValidators,
  orderValidators,
  reviewValidators,
  promoCodeValidators,
  paymentValidators,
  zoneValidators,
  riderValidators,
  mongoIdParam,
  paginationQuery,
};
