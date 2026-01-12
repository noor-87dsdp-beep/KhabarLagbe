# Backend API - KhabarLagbe Server

## Overview
RESTful API and WebSocket server for KhabarLagbe food delivery platform.

## Tech Stack
- **Runtime**: Node.js with Express / Python with Django / FastAPI
- **Database**: PostgreSQL with Prisma ORM / MongoDB
- **Cache**: Redis
- **Real-time**: Socket.IO
- **Authentication**: JWT + Refresh Tokens
- **Storage**: AWS S3 / Firebase Storage
- **Payment**: Stripe / Razorpay
- **Maps**: Mapbox API
- **Notifications**: Firebase Cloud Messaging, Twilio (SMS)
- **Email**: SendGrid
- **Queue**: Bull with Redis
- **Monitoring**: Sentry, New Relic

## API Endpoints

### Authentication
```
POST   /api/v1/auth/register              - User registration
POST   /api/v1/auth/login                 - Login
POST   /api/v1/auth/refresh               - Refresh access token
POST   /api/v1/auth/logout                - Logout
POST   /api/v1/auth/forgot-password       - Forgot password
POST   /api/v1/auth/reset-password        - Reset password
POST   /api/v1/auth/verify-email          - Verify email
POST   /api/v1/auth/verify-phone          - Verify phone (OTP)
```

### Users
```
GET    /api/v1/users/profile              - Get user profile
PUT    /api/v1/users/profile              - Update profile
POST   /api/v1/users/addresses            - Add address
PUT    /api/v1/users/addresses/:id        - Update address
DELETE /api/v1/users/addresses/:id        - Delete address
GET    /api/v1/users/orders               - Order history
GET    /api/v1/users/favorites            - Favorite restaurants
POST   /api/v1/users/favorites/:id        - Add to favorites
DELETE /api/v1/users/favorites/:id        - Remove from favorites
```

### Restaurants
```
GET    /api/v1/restaurants                - List restaurants (with filters)
GET    /api/v1/restaurants/:id            - Restaurant details
GET    /api/v1/restaurants/:id/menu       - Restaurant menu
GET    /api/v1/restaurants/:id/reviews    - Restaurant reviews
POST   /api/v1/restaurants/:id/reviews    - Add review
GET    /api/v1/restaurants/search         - Search restaurants
POST   /api/v1/restaurants                - Create restaurant (admin)
PUT    /api/v1/restaurants/:id            - Update restaurant
```

### Menu
```
POST   /api/v1/restaurants/:id/menu       - Add menu item
PUT    /api/v1/restaurants/:id/menu/:itemId - Update menu item
DELETE /api/v1/restaurants/:id/menu/:itemId - Delete menu item
PATCH  /api/v1/restaurants/:id/menu/:itemId/availability - Toggle availability
```

### Orders
```
POST   /api/v1/orders                     - Place order
GET    /api/v1/orders/:id                 - Order details
GET    /api/v1/orders                     - List orders
PATCH  /api/v1/orders/:id/status          - Update order status
POST   /api/v1/orders/:id/cancel          - Cancel order
POST   /api/v1/orders/:id/rate            - Rate order
```

### Cart
```
POST   /api/v1/cart/items                 - Add item to cart
PUT    /api/v1/cart/items/:id             - Update cart item
DELETE /api/v1/cart/items/:id             - Remove from cart
DELETE /api/v1/cart                        - Clear cart
POST   /api/v1/cart/promo                 - Apply promo code
```

### Payments
```
POST   /api/v1/payments/intent            - Create payment intent
POST   /api/v1/payments/confirm           - Confirm payment
GET    /api/v1/payments/:id               - Payment details
POST   /api/v1/payments/:id/refund        - Process refund
```

### Riders
```
GET    /api/v1/riders/orders              - Available orders for rider
POST   /api/v1/riders/orders/:id/accept   - Accept order
POST   /api/v1/riders/orders/:id/reject   - Reject order
PATCH  /api/v1/riders/location            - Update rider location
GET    /api/v1/riders/earnings            - Rider earnings
GET    /api/v1/riders/profile             - Rider profile
```

### Admin
```
GET    /api/v1/admin/dashboard            - Dashboard stats
GET    /api/v1/admin/users                - All users
GET    /api/v1/admin/restaurants          - All restaurants
GET    /api/v1/admin/riders               - All riders
GET    /api/v1/admin/orders               - All orders
POST   /api/v1/admin/restaurants/:id/approve - Approve restaurant
POST   /api/v1/admin/riders/:id/approve   - Approve rider
POST   /api/v1/admin/promo-codes          - Create promo code
GET    /api/v1/admin/analytics            - Analytics data
```

### Geocoding (Mapbox Integration)
```
GET    /api/v1/geocode/search             - Address search
GET    /api/v1/geocode/reverse            - Reverse geocode
POST   /api/v1/geocode/directions         - Get directions
POST   /api/v1/geocode/distance-matrix    - Calculate distances
```

## WebSocket Events

### Order Tracking
```javascript
// Client subscribes to order updates
socket.emit('subscribe:order', { orderId })

// Server sends updates
socket.on('order:status', (data) => {
  // { orderId, status, timestamp }
})

socket.on('rider:location', (data) => {
  // { orderId, latitude, longitude, timestamp }
})
```

### Restaurant Orders
```javascript
// Restaurant subscribes to incoming orders
socket.emit('subscribe:restaurant', { restaurantId })

// Server sends new orders
socket.on('order:new', (data) => {
  // { order details }
})
```

### Rider Updates
```javascript
// Rider sends location updates
socket.emit('rider:location', { 
  latitude, 
  longitude, 
  accuracy, 
  timestamp 
})

// Rider receives new order notifications
socket.on('order:available', (data) => {
  // { order details }
})
```

## Database Schema

### Users
```sql
users {
  id: UUID PK
  name: String
  email: String UNIQUE
  phone: String UNIQUE
  password_hash: String
  role: Enum (user, rider, restaurant, admin)
  profile_image_url: String
  email_verified: Boolean
  phone_verified: Boolean
  created_at: Timestamp
  updated_at: Timestamp
}
```

### Addresses
```sql
addresses {
  id: UUID PK
  user_id: UUID FK(users.id)
  label: String
  address_line1: String
  address_line2: String
  city: String
  state: String
  zip_code: String
  latitude: Float
  longitude: Float
  is_default: Boolean
  created_at: Timestamp
}
```

### Restaurants
```sql
restaurants {
  id: UUID PK
  user_id: UUID FK(users.id)
  name: String
  description: Text
  image_url: String
  cover_image_url: String
  cuisine: String[]
  rating: Float
  total_reviews: Integer
  delivery_fee: Decimal
  min_order_amount: Decimal
  latitude: Float
  longitude: Float
  address: String
  is_open: Boolean
  is_active: Boolean
  commission_rate: Decimal
  created_at: Timestamp
  updated_at: Timestamp
}
```

### Menu Items
```sql
menu_items {
  id: UUID PK
  restaurant_id: UUID FK(restaurants.id)
  category_id: UUID FK(menu_categories.id)
  name: String
  description: Text
  price: Decimal
  image_url: String
  is_vegetarian: Boolean
  is_vegan: Boolean
  is_gluten_free: Boolean
  is_available: Boolean
  preparation_time: Integer
  created_at: Timestamp
  updated_at: Timestamp
}
```

### Orders
```sql
orders {
  id: UUID PK
  user_id: UUID FK(users.id)
  restaurant_id: UUID FK(restaurants.id)
  rider_id: UUID FK(riders.id)
  delivery_address_id: UUID FK(addresses.id)
  status: Enum
  subtotal: Decimal
  delivery_fee: Decimal
  tax: Decimal
  discount: Decimal
  total: Decimal
  payment_method: Enum
  payment_status: Enum
  special_instructions: Text
  created_at: Timestamp
  updated_at: Timestamp
  delivered_at: Timestamp
}
```

### Order Items
```sql
order_items {
  id: UUID PK
  order_id: UUID FK(orders.id)
  menu_item_id: UUID FK(menu_items.id)
  quantity: Integer
  price: Decimal
  customizations: JSON
  special_instructions: Text
}
```

## Authentication Flow
1. User registers with email/phone
2. Verification email/SMS sent
3. User verifies account
4. Login returns access token (15 min) and refresh token (7 days)
5. Access token used for authenticated requests
6. Refresh token used to get new access token
7. Logout invalidates refresh token

## Order Flow
1. User adds items to cart
2. User proceeds to checkout
3. User selects delivery address
4. User applies promo code (optional)
5. User selects payment method
6. Order created with status PENDING
7. Payment processed
8. Order status → CONFIRMED
9. Restaurant receives notification
10. Restaurant accepts → PREPARING
11. Restaurant marks ready → READY_FOR_PICKUP
12. Rider assigned and notified
13. Rider accepts and arrives at restaurant
14. Rider picks up → PICKED_UP
15. Rider navigates to customer → ON_THE_WAY
16. Rider delivers → DELIVERED
17. Customer rates order

## Real-time Features
- Live order tracking
- Rider location updates every 10 seconds
- Restaurant order notifications
- Push notifications for status changes
- In-app messaging

## File Structure
```
backend/
├── src/
│   ├── controllers/
│   ├── models/
│   ├── routes/
│   ├── middleware/
│   ├── services/
│   ├── utils/
│   ├── config/
│   └── socket/
├── tests/
├── prisma/
│   └── schema.prisma
├── .env.example
├── package.json
└── README.md
```

## Environment Variables
```env
# Server
NODE_ENV=development
PORT=3000
API_VERSION=v1

# Database
DATABASE_URL=postgresql://user:password@localhost:5432/khabarlagbe

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=15m
REFRESH_TOKEN_SECRET=your-refresh-secret
REFRESH_TOKEN_EXPIRES_IN=7d

# Mapbox
MAPBOX_ACCESS_TOKEN=your-mapbox-token

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY=your-private-key
FIREBASE_CLIENT_EMAIL=your-client-email

# Payment
STRIPE_SECRET_KEY=your-stripe-key
STRIPE_WEBHOOK_SECRET=your-webhook-secret

# AWS S3
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_BUCKET_NAME=your-bucket-name
AWS_REGION=us-east-1

# Email
SENDGRID_API_KEY=your-sendgrid-key
FROM_EMAIL=noreply@khabarlagbe.com

# SMS
TWILIO_ACCOUNT_SID=your-account-sid
TWILIO_AUTH_TOKEN=your-auth-token
TWILIO_PHONE_NUMBER=+1234567890
```

## Deployment
- **Hosting**: AWS EC2, DigitalOcean, or Heroku
- **Database**: AWS RDS, DigitalOcean Managed PostgreSQL
- **Cache**: AWS ElastiCache, Redis Cloud
- **Storage**: AWS S3, Cloudinary
- **CDN**: CloudFront, Cloudflare
- **CI/CD**: GitHub Actions, GitLab CI
- **Monitoring**: Sentry, DataDog, New Relic
- **Logging**: CloudWatch, Loggly

## Security Best Practices
- Input validation
- SQL injection prevention
- XSS protection
- CSRF protection
- Rate limiting
- API key rotation
- Encrypted passwords (bcrypt)
- HTTPS only
- CORS configuration
- Security headers
- Regular dependency updates
