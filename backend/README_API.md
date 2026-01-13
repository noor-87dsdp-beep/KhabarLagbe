# KhabarLagbe Backend API

Complete Node.js/Express backend for KhabarLagbe food delivery platform with Bangladesh-specific payment integrations.

## 🚀 Features

- ✅ **Authentication** - Phone OTP-based authentication (Bangladesh format)
- ✅ **User Management** - Profile, addresses, favorites
- ✅ **Restaurant Management** - CRUD, search, geospatial queries
- ✅ **Order Management** - Create, track, cancel orders
- ✅ **Payment Integration** - bKash, Nagad, SSL Commerz (cards), Cash on Delivery
- ✅ **Real-time Updates** - Socket.IO for order tracking and rider location
- ✅ **Notifications** - Firebase Cloud Messaging push notifications
- ✅ **Caching** - Redis for OTP and session management
- ✅ **Security** - Helmet, rate limiting, JWT authentication
- ✅ **Database** - MongoDB with Mongoose ODM

## 📁 Project Structure

```
backend/
├── src/
│   ├── config/          # Database, Redis, Firebase, Socket.IO
│   ├── controllers/     # Route handlers
│   ├── models/          # Mongoose schemas
│   ├── routes/          # API routes
│   ├── middleware/      # Auth, validation, error handling
│   ├── services/        # Business logic (payments, OTP, notifications)
│   ├── utils/           # Helper functions
│   ├── app.js           # Express app setup
│   └── server.js        # Server entry point
├── Dockerfile
├── docker-compose.yml
├── package.json
└── .env.example
```

## 🛠️ Tech Stack

- **Runtime:** Node.js 18+
- **Framework:** Express.js 4.18
- **Database:** MongoDB 8.0
- **Cache:** Redis 4.6
- **ODM:** Mongoose 8.0
- **Real-time:** Socket.IO 4.7
- **Authentication:** JWT (jsonwebtoken 9.0)
- **Validation:** express-validator 7.0
- **Security:** Helmet 7.1, express-rate-limit 7.1
- **Notifications:** Firebase Admin SDK 12.0

## 📦 Installation

### Prerequisites

- Node.js 18+ and npm
- MongoDB 7.0+
- Redis 7.0+
- Firebase project (for notifications)

### Local Setup

1. **Clone the repository**
```bash
cd backend
```

2. **Install dependencies**
```bash
npm install
```

3. **Configure environment variables**
```bash
cp .env.example .env
# Edit .env with your configuration
```

4. **Start MongoDB and Redis** (if not using Docker)
```bash
# MongoDB
mongod --dbpath /path/to/data

# Redis
redis-server
```

5. **Run the server**
```bash
# Development
npm run dev

# Production
npm start
```

### Docker Setup

1. **Build and run with Docker Compose**
```bash
docker-compose up -d
```

2. **View logs**
```bash
docker-compose logs -f api
```

3. **Stop containers**
```bash
docker-compose down
```

## 🔧 Environment Variables

Key environment variables (see `.env.example` for complete list):

```env
# Server
NODE_ENV=development
PORT=3000
BASE_URL=http://localhost:3000

# Database
DATABASE_URL=mongodb://localhost:27017/khabarlagbe

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=your-jwt-secret-min-32-chars
JWT_EXPIRES_IN=15m
REFRESH_TOKEN_SECRET=your-refresh-token-secret
REFRESH_TOKEN_EXPIRES_IN=7d

# Firebase (for push notifications)
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk@your-project.iam.gserviceaccount.com

# Payment Gateways
# bKash
BKASH_BASE_URL=https://checkout.sandbox.bkash.com/v1.2.0-beta
BKASH_APP_KEY=your-app-key
BKASH_APP_SECRET=your-app-secret
BKASH_USERNAME=your-username
BKASH_PASSWORD=your-password

# Nagad
NAGAD_MERCHANT_ID=your-merchant-id
NAGAD_MERCHANT_NUMBER=your-merchant-number

# SSL Commerz
SSLCOMMERZ_STORE_ID=your-store-id
SSLCOMMERZ_STORE_PASSWORD=your-store-password
SSLCOMMERZ_IS_LIVE=false

# OTP
OTP_EXPIRY_MINUTES=10
OTP_LENGTH=6
```

## 📡 API Endpoints

### Authentication

```
POST   /api/v1/auth/send-otp       # Send OTP to phone
POST   /api/v1/auth/verify-otp     # Verify OTP and login/register
POST   /api/v1/auth/refresh-token  # Refresh access token
POST   /api/v1/auth/logout          # Logout
```

### Users

```
GET    /api/v1/users/profile        # Get user profile
PATCH  /api/v1/users/profile        # Update profile
POST   /api/v1/users/addresses      # Add address
PATCH  /api/v1/users/addresses/:id  # Update address
DELETE /api/v1/users/addresses/:id  # Delete address
POST   /api/v1/users/favorites/:id  # Toggle favorite restaurant
GET    /api/v1/users/favorites      # Get favorite restaurants
```

### Restaurants

```
GET    /api/v1/restaurants          # List restaurants (with filters)
GET    /api/v1/restaurants/featured # Get featured restaurants
GET    /api/v1/restaurants/search   # Search restaurants and menu items
GET    /api/v1/restaurants/:id      # Get restaurant details with menu
```

### Orders

```
POST   /api/v1/orders               # Create order
GET    /api/v1/orders               # Get user orders
GET    /api/v1/orders/:id           # Get order details
GET    /api/v1/orders/:id/track     # Track order
PATCH  /api/v1/orders/:id/cancel    # Cancel order
```

## 🔐 Authentication

The API uses JWT-based authentication with phone OTP verification.

### Flow:

1. **Send OTP**
```bash
POST /api/v1/auth/send-otp
{
  "phone": "+8801712345678"
}
```

2. **Verify OTP** (returns access and refresh tokens)
```bash
POST /api/v1/auth/verify-otp
{
  "phone": "+8801712345678",
  "otp": "123456"
}
```

3. **Use access token** in subsequent requests
```bash
Authorization: Bearer <access_token>
```

4. **Refresh token** when access token expires
```bash
POST /api/v1/auth/refresh-token
{
  "refreshToken": "<refresh_token>"
}
```

## 💳 Payment Integration

### Supported Payment Methods

1. **bKash** - Most popular mobile payment in Bangladesh
2. **Nagad** - Government-backed mobile payment
3. **Rocket** - Dutch-Bangla Bank mobile payment
4. **Cards** - Visa/Mastercard via SSL Commerz
5. **Cash on Delivery** - Traditional COD

### Payment Flow Example (bKash)

```javascript
// 1. Create payment
const payment = await bkashService.createPayment(total, orderId, phone);
// Returns: { paymentId, bkashURL }

// 2. Redirect user to bkashURL

// 3. After user completes payment, execute
const result = await bkashService.executePayment(paymentId);
// Returns: { success, transactionId }
```

## 🔌 WebSocket Events

Real-time updates via Socket.IO on `/order` and `/rider` namespaces.

### Order Tracking
```javascript
// Client subscribes to order updates
socket.emit('subscribe', orderId);

// Receive order status updates
socket.on('order:status_changed', (data) => {
  // { orderId, status, timestamp }
});

// Receive rider location updates
socket.on('rider:location', (data) => {
  // { riderId, lat, lng, timestamp }
});
```

## 📊 Database Schema

### User
- phone (unique, BD format)
- name, email, profileImage
- addresses (array)
- favorites (restaurant IDs)
- preferredLanguage (en/bn)

### Restaurant
- name, description, cuisines
- location (GeoJSON Point)
- address, businessHours
- rating, totalReviews
- deliveryTime, minOrderAmount

### Order
- user, restaurant, rider
- items (array of menu items)
- deliveryAddress
- subtotal, deliveryFee, vat, discount, total
- paymentMethod, paymentStatus
- status, statusHistory
- timestamps

## 🧪 Testing

```bash
# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Run linter
npm run lint
```

## 🚀 Deployment

### Production Checklist

- [ ] Set `NODE_ENV=production`
- [ ] Use strong JWT secrets
- [ ] Configure production MongoDB and Redis
- [ ] Set up SSL certificates
- [ ] Configure Firebase for production
- [ ] Set up payment gateway production credentials
- [ ] Enable logging service (Winston → file/cloud)
- [ ] Set up monitoring (New Relic, Sentry)
- [ ] Configure CORS for your domains
- [ ] Set up automatic backups

### Deploy to VPS/Cloud

```bash
# 1. Clone repository
git clone https://github.com/your-repo/khabarlagbe.git
cd khabarlagbe/backend

# 2. Install dependencies
npm ci --only=production

# 3. Set environment variables
cp .env.example .env
nano .env

# 4. Use process manager (PM2)
npm install -g pm2
pm2 start src/server.js --name khabarlagbe-api
pm2 save
pm2 startup
```

## 📈 Performance

- **Caching:** Redis for OTP and frequently accessed data
- **Database Indexing:** Geospatial, text search, composite indexes
- **Rate Limiting:** Protect against abuse
- **Compression:** gzip compression for responses
- **Connection Pooling:** MongoDB connection pooling

## 🔒 Security

- Helmet.js for HTTP headers
- Rate limiting on sensitive endpoints
- JWT token expiration
- Input validation with express-validator
- MongoDB injection prevention
- CORS configuration
- Environment variables for secrets

## 📝 API Documentation

For detailed API documentation with request/response examples, visit:
- Postman Collection: `docs/postman/`
- Swagger/OpenAPI: Coming soon

## 🐛 Troubleshooting

### MongoDB connection failed
```bash
# Check if MongoDB is running
mongod --version
# Start MongoDB
mongod --dbpath /path/to/data
```

### Redis connection failed
```bash
# Check if Redis is running
redis-cli ping
# Should return PONG
```

### OTP not working in development
```bash
# Check console logs for OTP
# OTPs are logged in development mode
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

MIT License - see LICENSE file for details

## 📞 Support

- **Email:** support@khabarlagbe.com
- **GitHub Issues:** [Create an issue](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues)

---

**Built with ❤️ for Bangladesh 🇧🇩**
