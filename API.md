# API Documentation - KhabarLagbe

## Base URL

```
Development: http://localhost:3000/api/v1
Production: https://api.khabarlagbe.com/api/v1
```

## Authentication

All authenticated endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer <access_token>
```

### Token Structure
- **Access Token**: Short-lived (15 minutes), used for API requests
- **Refresh Token**: Long-lived (7 days), used to get new access tokens

## Response Format

### Success Response
```json
{
  "success": true,
  "data": { },
  "message": "Operation successful"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description",
    "details": []
  }
}
```

## Error Codes

| Code | Description |
|------|-------------|
| AUTH_001 | Invalid credentials |
| AUTH_002 | Token expired |
| AUTH_003 | Invalid token |
| USER_001 | User not found |
| USER_002 | User already exists |
| REST_001 | Restaurant not found |
| ORDER_001 | Order not found |
| ORDER_002 | Invalid order status |
| PAYMENT_001 | Payment failed |
| VALIDATION_001 | Invalid input data |

---

## Authentication Endpoints

### Register User

```http
POST /auth/register
```

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_123",
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "+1234567890"
    },
    "tokens": {
      "accessToken": "eyJhbGc...",
      "refreshToken": "eyJhbGc...",
      "expiresIn": 900
    }
  }
}
```

### Login

```http
POST /auth/login
```

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": { },
    "tokens": { }
  }
}
```

### Refresh Token

```http
POST /auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "expiresIn": 900
  }
}
```

### Logout

```http
POST /auth/logout
```

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

## User Endpoints

### Get User Profile

```http
GET /users/profile
```

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "usr_123",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "profileImageUrl": "https://...",
    "addresses": [],
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### Update Profile

```http
PUT /users/profile
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "name": "John Updated",
  "profileImageUrl": "https://..."
}
```

### Add Address

```http
POST /users/addresses
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "label": "Home",
  "addressLine1": "123 Main St",
  "addressLine2": "Apt 4B",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "isDefault": true
}
```

---

## Restaurant Endpoints

### List Restaurants

```http
GET /restaurants?lat=40.7128&lng=-74.0060&radius=5&cuisine=Italian&sort=rating
```

**Query Parameters:**
- `lat` (required): Latitude
- `lng` (required): Longitude
- `radius` (optional): Search radius in km (default: 10)
- `cuisine` (optional): Filter by cuisine type
- `sort` (optional): Sort by `rating`, `distance`, `deliveryTime` (default: rating)
- `minRating` (optional): Minimum rating (0-5)
- `page` (optional): Page number (default: 1)
- `limit` (optional): Results per page (default: 20, max: 50)

**Response:**
```json
{
  "success": true,
  "data": {
    "restaurants": [
      {
        "id": "rest_123",
        "name": "Pizza Paradise",
        "description": "Authentic Italian pizzas",
        "imageUrl": "https://...",
        "cuisine": ["Italian", "Pizza"],
        "rating": 4.5,
        "totalReviews": 230,
        "deliveryTime": 30,
        "deliveryFee": 0,
        "minOrderAmount": 100,
        "isOpen": true,
        "distance": 2.5,
        "latitude": 40.7128,
        "longitude": -74.0060
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 45,
      "totalPages": 3
    }
  }
}
```

### Get Restaurant Details

```http
GET /restaurants/:id
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "rest_123",
    "name": "Pizza Paradise",
    "description": "Authentic Italian pizzas",
    "imageUrl": "https://...",
    "coverImageUrl": "https://...",
    "cuisine": ["Italian", "Pizza"],
    "rating": 4.5,
    "totalReviews": 230,
    "deliveryTime": 30,
    "deliveryFee": 0,
    "minOrderAmount": 100,
    "isOpen": true,
    "distance": 2.5,
    "address": "123 Main St, New York, NY",
    "phone": "+1234567890",
    "hours": {
      "monday": { "open": "10:00", "close": "22:00" },
      "tuesday": { "open": "10:00", "close": "22:00" }
    },
    "categories": []
  }
}
```

### Get Restaurant Menu

```http
GET /restaurants/:id/menu
```

**Response:**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "id": "cat_123",
        "name": "Pizzas",
        "items": [
          {
            "id": "item_123",
            "name": "Margherita Pizza",
            "description": "Classic pizza with tomato and mozzarella",
            "price": 12.99,
            "imageUrl": "https://...",
            "isVegetarian": true,
            "isVegan": false,
            "isGlutenFree": false,
            "customizations": [
              {
                "id": "cust_123",
                "name": "Size",
                "type": "SINGLE_SELECT",
                "isRequired": true,
                "options": [
                  {
                    "id": "opt_123",
                    "name": "Medium",
                    "additionalPrice": 0
                  },
                  {
                    "id": "opt_124",
                    "name": "Large",
                    "additionalPrice": 3.00
                  }
                ]
              }
            ],
            "isAvailable": true
          }
        ]
      }
    ]
  }
}
```

---

## Order Endpoints

### Place Order

```http
POST /orders
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "restaurantId": "rest_123",
  "items": [
    {
      "menuItemId": "item_123",
      "quantity": 2,
      "customizations": [
        {
          "optionId": "cust_123",
          "choiceId": "opt_124"
        }
      ],
      "specialInstructions": "Extra cheese"
    }
  ],
  "deliveryAddressId": "addr_123",
  "paymentMethod": "CREDIT_CARD",
  "promoCode": "FIRST30",
  "specialInstructions": "Ring doorbell twice"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "order": {
      "id": "ord_123",
      "status": "PENDING",
      "restaurant": { },
      "items": [],
      "deliveryAddress": { },
      "subtotal": 25.98,
      "deliveryFee": 0,
      "tax": 2.08,
      "discount": 7.79,
      "total": 20.27,
      "paymentMethod": "CREDIT_CARD",
      "paymentStatus": "PENDING",
      "estimatedDeliveryTime": "2024-01-01T13:30:00Z",
      "createdAt": "2024-01-01T12:00:00Z"
    },
    "paymentIntent": {
      "clientSecret": "pi_123_secret_456"
    }
  }
}
```

### Get Order Details

```http
GET /orders/:id
```

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "order": { },
    "tracking": {
      "status": "ON_THE_WAY",
      "riderLocation": {
        "latitude": 40.7128,
        "longitude": -74.0060,
        "timestamp": "2024-01-01T13:15:00Z"
      },
      "estimatedArrival": "2024-01-01T13:30:00Z",
      "timeline": [
        {
          "status": "PENDING",
          "timestamp": "2024-01-01T12:00:00Z",
          "message": "Order placed"
        },
        {
          "status": "CONFIRMED",
          "timestamp": "2024-01-01T12:02:00Z",
          "message": "Restaurant confirmed your order"
        }
      ]
    },
    "rider": {
      "id": "rider_123",
      "name": "Mike Driver",
      "phone": "+1234567890",
      "rating": 4.8,
      "vehicleNumber": "ABC-123"
    }
  }
}
```

### List User Orders

```http
GET /orders?status=COMPLETED&page=1&limit=10
```

**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
- `status` (optional): Filter by order status
- `page` (optional): Page number
- `limit` (optional): Results per page

**Response:**
```json
{
  "success": true,
  "data": {
    "orders": [],
    "pagination": { }
  }
}
```

### Cancel Order

```http
POST /orders/:id/cancel
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "reason": "Changed my mind"
}
```

### Rate Order

```http
POST /orders/:id/rate
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "restaurantRating": 5,
  "riderRating": 4,
  "comment": "Great food, fast delivery!",
  "foodQuality": 5,
  "packaging": 5
}
```

---

## Payment Endpoints

### Create Payment Intent

```http
POST /payments/intent
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "orderId": "ord_123",
  "amount": 20.27
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "clientSecret": "pi_123_secret_456",
    "paymentIntentId": "pi_123"
  }
}
```

### Confirm Payment

```http
POST /payments/confirm
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "paymentIntentId": "pi_123",
  "paymentMethodId": "pm_123"
}
```

---

## Rider Endpoints

### Get Available Orders (Rider)

```http
GET /riders/orders?lat=40.7128&lng=-74.0060
```

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "id": "ord_123",
        "restaurant": { },
        "deliveryAddress": { },
        "distance": 3.2,
        "estimatedEarnings": 5.50,
        "pickupTime": "2024-01-01T13:00:00Z"
      }
    ]
  }
}
```

### Accept Order (Rider)

```http
POST /riders/orders/:id/accept
```

**Headers:** `Authorization: Bearer <token>`

### Update Rider Location

```http
PATCH /riders/location
```

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "accuracy": 10.5,
  "timestamp": "2024-01-01T13:15:00Z"
}
```

---

## Admin Endpoints

### Get Dashboard Stats

```http
GET /admin/dashboard
```

**Headers:** `Authorization: Bearer <admin_token>`

**Response:**
```json
{
  "success": true,
  "data": {
    "activeOrders": 45,
    "totalUsers": 1250,
    "totalRestaurants": 180,
    "totalRiders": 95,
    "todayRevenue": 12450.50,
    "weekRevenue": 78920.75
  }
}
```

---

## WebSocket Events

### Connect

```javascript
const socket = io('ws://localhost:3000', {
  auth: {
    token: 'Bearer <access_token>'
  }
});
```

### Order Tracking (User)

```javascript
// Subscribe to order updates
socket.emit('subscribe:order', { orderId: 'ord_123' });

// Listen for order status updates
socket.on('order:status', (data) => {
  console.log(data);
  // {
  //   orderId: 'ord_123',
  //   status: 'ON_THE_WAY',
  //   timestamp: '2024-01-01T13:15:00Z'
  // }
});

// Listen for rider location updates
socket.on('rider:location', (data) => {
  console.log(data);
  // {
  //   orderId: 'ord_123',
  //   latitude: 40.7128,
  //   longitude: -74.0060,
  //   timestamp: '2024-01-01T13:15:00Z'
  // }
});
```

### Restaurant Orders

```javascript
// Subscribe to new orders
socket.emit('subscribe:restaurant', { restaurantId: 'rest_123' });

// Listen for new orders
socket.on('order:new', (data) => {
  console.log('New order:', data);
});
```

### Rider Location Updates

```javascript
// Send location updates
socket.emit('rider:location', {
  latitude: 40.7128,
  longitude: -74.0060,
  accuracy: 10.5,
  timestamp: Date.now()
});

// Receive new order notifications
socket.on('order:available', (data) => {
  console.log('New order available:', data);
});
```

---

## Rate Limiting

- **Standard**: 100 requests per 15 minutes
- **Authentication**: 5 requests per 15 minutes
- **Order Placement**: 10 orders per hour

## Webhooks

### Stripe Payment Webhook

```http
POST /webhooks/stripe
```

**Events:**
- `payment_intent.succeeded`
- `payment_intent.failed`
- `charge.refunded`

---

**Last Updated**: January 2026
