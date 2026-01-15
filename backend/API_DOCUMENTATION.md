# KhabarLagbe API Documentation

## Base URL
```
Development: http://localhost:5000/api/v1
Production: https://api.khabarlagbe.com/api/v1
```

## Authentication
Most endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## 🔐 Authentication Endpoints

### POST /auth/send-otp
Send OTP to phone number for authentication.

**Request Body:**
```json
{
  "phone": "+8801712345678"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OTP sent successfully"
}
```

### POST /auth/verify-otp
Verify OTP and get authentication tokens.

**Request Body:**
```json
{
  "phone": "+8801712345678",
  "otp": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "user": {
      "_id": "...",
      "name": "John Doe",
      "phone": "+8801712345678",
      "role": "customer"
    }
  }
}
```

### POST /auth/refresh
Refresh access token using refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

### POST /auth/logout
Logout user (invalidate refresh token).

---

## 👤 User Endpoints

### GET /users/profile
Get current user profile. **[Auth Required]**

### PUT /users/profile
Update user profile. **[Auth Required]**

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

### GET /users/addresses
Get user addresses. **[Auth Required]**

### POST /users/addresses
Add new address. **[Auth Required]**

**Request Body:**
```json
{
  "label": "Home",
  "houseNo": "123",
  "roadNo": "5",
  "area": "Gulshan",
  "thana": "Gulshan",
  "district": "Dhaka",
  "division": "Dhaka",
  "postalCode": "1212",
  "latitude": 23.7809,
  "longitude": 90.4144
}
```

---

## 🍽️ Restaurant Endpoints

### GET /restaurants
Get all restaurants with filtering.

**Query Parameters:**
- `page` - Page number (default: 1)
- `limit` - Items per page (default: 20)
- `latitude` - User latitude for nearby search
- `longitude` - User longitude for nearby search
- `maxDistance` - Max distance in meters (default: 5000)
- `search` - Search by name or cuisine
- `cuisines` - Filter by cuisine types (comma-separated)
- `minRating` - Minimum rating filter

### GET /restaurants/:id
Get restaurant details by ID.

### GET /restaurants/:id/menu
Get restaurant menu with categories and items.

### GET /restaurants/featured
Get featured/promoted restaurants.

### POST /restaurants
Create new restaurant. **[Admin Auth Required]**

---

## 📦 Order Endpoints

### POST /orders
Create new order. **[Auth Required]**

**Request Body:**
```json
{
  "restaurantId": "...",
  "items": [
    {
      "menuItemId": "...",
      "quantity": 2,
      "customizations": [
        {
          "name": "Spice Level",
          "option": "Medium"
        }
      ]
    }
  ],
  "deliveryAddress": {
    "houseNo": "123",
    "area": "Gulshan",
    "phone": "+8801712345678"
  },
  "paymentMethod": "bkash",
  "specialInstructions": "Ring the bell twice",
  "promoCode": "FIRST50"
}
```

### GET /orders
Get user's order history. **[Auth Required]**

### GET /orders/:id
Get order details by ID. **[Auth Required]**

### POST /orders/:id/cancel
Cancel an order. **[Auth Required]**

### PATCH /orders/:id/status
Update order status. **[Restaurant/Rider/Admin Auth Required]**

---

## 🏍️ Rider Endpoints

### POST /riders/register
Register as a rider.

**Request Body:**
```json
{
  "name": "Ahmed Khan",
  "phone": "+8801712345678",
  "email": "ahmed@example.com",
  "nid": "1234567890",
  "drivingLicense": "DH-123456",
  "vehicleType": "motorcycle",
  "vehicleNumber": "DHA-1234",
  "zone": "Gulshan"
}
```

### GET /riders/profile
Get rider profile. **[Rider Auth Required]**

### PATCH /riders/status
Update online/offline status. **[Rider Auth Required]**

**Request Body:**
```json
{
  "status": "available"
}
```

### PATCH /riders/location
Update rider location. **[Rider Auth Required]**

**Request Body:**
```json
{
  "latitude": 23.7809,
  "longitude": 90.4144
}
```

### GET /riders/available-orders
Get orders available for pickup. **[Rider Auth Required]**

### POST /riders/orders/:orderId/accept
Accept an order. **[Rider Auth Required]**

### GET /riders/earnings
Get earnings statistics. **[Rider Auth Required]**

**Query Parameters:**
- `startDate` - Start date (ISO 8601)
- `endDate` - End date (ISO 8601)

---

## 💳 Payment Endpoints

### POST /payments/create
Initiate payment for an order. **[Auth Required]**

**Request Body:**
```json
{
  "orderId": "...",
  "method": "bkash",
  "returnUrl": "https://app.khabarlagbe.com/payment/success"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "payment": { ... },
    "redirectUrl": "https://sandbox.bka sh.com/..."
  }
}
```

### POST /payments/bkash/callback
bKash payment callback (called by bKash gateway).

### POST /payments/nagad/callback
Nagad payment callback (called by Nagad gateway).

### POST /payments/sslcommerz/callback
SSL Commerz callback (called by SSL Commerz).

### GET /payments/history
Get user's payment history. **[Auth Required]**

### POST /payments/:paymentId/refund
Refund a payment. **[Admin Auth Required]**

---

## ⭐ Review Endpoints

### POST /reviews
Submit review for delivered order. **[Auth Required]**

**Request Body:**
```json
{
  "orderId": "...",
  "foodRating": 5,
  "deliveryRating": 4,
  "review": "Excellent food!",
  "images": ["url1.jpg", "url2.jpg"]
}
```

### GET /reviews/restaurant/:restaurantId
Get reviews for a restaurant.

### GET /reviews/my-reviews
Get user's reviews. **[Auth Required]**

### PUT /reviews/:reviewId
Update a review. **[Auth Required]**

### DELETE /reviews/:reviewId
Delete a review. **[Auth Required]**

### POST /reviews/:reviewId/respond
Restaurant owner responds to review. **[Restaurant Auth Required]**

---

## 🎫 Promo Code Endpoints

### GET /promo-codes/active
Get active promo codes. **[Auth Required]**

### POST /promo-codes/validate
Validate a promo code. **[Auth Required]**

**Request Body:**
```json
{
  "code": "FIRST50",
  "restaurantId": "...",
  "orderAmount": 50000
}
```

### POST /promo-codes
Create promo code. **[Admin Auth Required]**

### GET /promo-codes/:promoId/stats
Get promo code statistics. **[Admin Auth Required]**

---

## 👨‍💼 Admin Endpoints

### GET /admin/dashboard
Get dashboard statistics. **[Admin Auth Required]**

**Response:**
```json
{
  "success": true,
  "data": {
    "today": {
      "orders": 145,
      "revenue": 250000
    },
    "totals": {
      "users": 1250,
      "restaurants": 85,
      "riders": 120,
      "orders": 5430
    },
    "pending": {
      "restaurants": 5,
      "riders": 8
    },
    "active": {
      "orders": 23,
      "riders": 45
    }
  }
}
```

### GET /admin/analytics
Get detailed analytics. **[Admin Auth Required]**

**Query Parameters:**
- `startDate` - Start date
- `endDate` - End date
- `type` - Time grouping (daily, monthly)

### GET /admin/revenue
Get revenue analytics. **[Admin Auth Required]**

### GET /admin/export
Export data (orders, payments, reviews). **[Admin Auth Required]**

**Query Parameters:**
- `type` - Data type to export
- `startDate` - Start date
- `endDate` - End date

---

## 📤 Upload Endpoints

### POST /upload/restaurant
Upload restaurant image. **[Auth Required]**

**Form Data:**
- `image` - Image file (max 5MB)

### POST /upload/menu
Upload menu item image. **[Auth Required]**

### POST /upload/profile
Upload profile picture. **[Auth Required]**

### POST /upload/rider/document
Upload rider document. **[Rider Auth Required]**

### POST /upload/delivery/proof
Upload delivery proof photo. **[Rider Auth Required]**

---

## 📍 Zone Endpoints

### GET /zones
Get all delivery zones.

### POST /zones
Create delivery zone. **[Admin Auth Required]**

**Request Body:**
```json
{
  "name": "Gulshan Area",
  "coordinates": {
    "type": "Polygon",
    "coordinates": [[[90.414, 23.780], ...]]
  },
  "deliveryFee": 5000,
  "deliveryFeePerKm": 1000
}
```

---

## 🍔 Menu & Categories

### GET /categories
Get all menu categories.

### POST /categories
Create category. **[Admin Auth Required]**

### GET /menu-items
Get menu items with filters.

### POST /menu-items
Create menu item. **[Restaurant Auth Required]**

---

## WebSocket Events

### Namespace: `/order`
**Events:**
- `subscribe` - Subscribe to order updates
- `order:status` - Order status changed
- `rider:location` - Rider location update

### Namespace: `/rider`
**Events:**
- `location` - Update rider location
- `order:new` - New order available

### Namespace: `/restaurant`
**Events:**
- `subscribe` - Subscribe to restaurant orders
- `order:new` - New order received

### Namespace: `/admin`
**Events:**
- `subscribe` - Subscribe to admin updates
- `order:created` - New order in system
- `rider:status` - Rider status changed

---

## Error Responses

All errors follow this format:
```json
{
  "success": false,
  "message": "Error description",
  "error": "ERROR_CODE"
}
```

### Common Error Codes
- `401` - Unauthorized (no/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `400` - Bad Request (validation error)
- `500` - Internal Server Error

---

## Rate Limiting
- OTP endpoints: 5 requests per 15 minutes
- Auth endpoints: 100 requests per 15 minutes
- Other endpoints: No limit (use responsibly)

---

## Data Formats

### Currency
All amounts are in Bangladeshi Taka paisa (1 Taka = 100 paisa)
- Example: ৳50.00 = 5000 paisa

### Phone Numbers
- Format: +880XXXXXXXXXX
- Example: +8801712345678

### Dates
- ISO 8601 format
- Example: 2024-01-15T10:30:00.000Z

---

## Support
For API support, contact: dev@khabarlagbe.com
