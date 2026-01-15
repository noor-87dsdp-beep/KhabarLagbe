# KhabarLagbe Backend API

Complete production-ready backend for Bangladesh food delivery platform.

## 🚀 Features

- **Complete REST API** with 60+ endpoints
- **Real-time Updates** via Socket.IO
- **Payment Integration** (bKash, Nagad, SSL Commerz)
- **File Upload** with Multer
- **Authentication** with JWT and OTP
- **Role-based Access** Control
- **Rate Limiting** and Security
- **MongoDB** Database with Mongoose
- **Redis** Caching
- **Firebase** Push Notifications

## 📋 Prerequisites

- Node.js 18+ and npm
- MongoDB 6.0+
- Redis 7.0+
- Firebase Account (for FCM)
- Payment Gateway Accounts (bKash, Nagad, SSL Commerz)

## 🔧 Installation

### 1. Install Dependencies

```bash
cd backend
npm install
```

### 2. Environment Configuration

Create `.env` file in the backend directory:

```bash
# Server Configuration
NODE_ENV=development
PORT=5000
API_VERSION=v1

# Database
MONGODB_URI=mongodb://localhost:27017/khabarlagbe
REDIS_URL=redis://localhost:6379

# JWT Secrets
JWT_ACCESS_SECRET=your_access_secret_key_here
JWT_REFRESH_SECRET=your_refresh_secret_key_here
JWT_ACCESS_EXPIRE=15m
JWT_REFRESH_EXPIRE=7d

# CORS
CORS_ORIGIN=http://localhost:3000,http://localhost:5173

# Firebase (FCM)
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_PRIVATE_KEY=your_private_key
FIREBASE_CLIENT_EMAIL=your_client_email

# bKash Payment Gateway
BKASH_APP_KEY=your_bkash_app_key
BKASH_APP_SECRET=your_bkash_app_secret
BKASH_USERNAME=your_bkash_username
BKASH_PASSWORD=your_bkash_password
BKASH_BASE_URL=https://tokenized.sandbox.bka sh.com

# Nagad Payment Gateway
NAGAD_MERCHANT_ID=your_merchant_id
NAGAD_MERCHANT_NUMBER=your_merchant_number
NAGAD_PUBLIC_KEY=your_public_key
NAGAD_PRIVATE_KEY=your_private_key
NAGAD_BASE_URL=http://sandbox.mynagad.com:10080/remote-payment-gateway-1.0

# SSL Commerz
SSLCOMMERZ_STORE_ID=your_store_id
SSLCOMMERZ_STORE_PASSWORD=your_store_password
SSLCOMMERZ_BASE_URL=https://sandbox.sslcommerz.com

# OTP Service (optional - for SMS)
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_PHONE_NUMBER=your_twilio_phone

# Email Service (optional)
SENDGRID_API_KEY=your_sendgrid_api_key
FROM_EMAIL=noreply@khabarlagbe.com

# Frontend URLs
FRONTEND_URL=http://localhost:3000
ADMIN_URL=http://localhost:5173
```

### 3. Start MongoDB

```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Or use local MongoDB installation
mongod --dbpath /data/db
```

### 4. Start Redis

```bash
# Using Docker
docker run -d -p 6379:6379 --name redis redis:latest

# Or use local Redis installation
redis-server
```

### 5. Run Database Migrations (if any)

```bash
npm run migrate
```

## 🏃 Running the Application

### Development Mode

```bash
npm run dev
```

Server will start on `http://localhost:5000`

### Production Mode

```bash
npm start
```

### Watch Mode (Auto-restart)

```bash
npm run watch
```

## 🧪 Testing

```bash
# Run all tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch
```

## 📚 API Documentation

Complete API documentation is available at:
- **File**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- **Live**: http://localhost:5000/api/v1 (when server is running)

### Quick API Examples

#### Send OTP
```bash
curl -X POST http://localhost:5000/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"phone": "+8801712345678"}'
```

#### Get Restaurants
```bash
curl -X GET "http://localhost:5000/api/v1/restaurants?latitude=23.7809&longitude=90.4144"
```

#### Create Order
```bash
curl -X POST http://localhost:5000/api/v1/orders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": "...",
    "items": [...],
    "deliveryAddress": {...},
    "paymentMethod": "bkash"
  }'
```

## 🏗️ Project Structure

```
backend/
├── src/
│   ├── config/           # Configuration files
│   │   ├── database.js   # MongoDB connection
│   │   ├── redis.js      # Redis connection
│   │   ├── firebase.js   # Firebase admin
│   │   └── socket.js     # Socket.IO setup
│   │
│   ├── models/           # Mongoose models (10 models)
│   │   ├── User.js
│   │   ├── Restaurant.js
│   │   ├── Order.js
│   │   ├── Rider.js
│   │   ├── Payment.js
│   │   ├── Review.js
│   │   ├── PromoCode.js
│   │   ├── MenuItem.js
│   │   ├── Category.js
│   │   └── Zone.js
│   │
│   ├── controllers/      # Business logic (13 controllers)
│   │   ├── authController.js
│   │   ├── userController.js
│   │   ├── restaurantController.js
│   │   ├── orderController.js
│   │   ├── riderController.js
│   │   ├── paymentController.js
│   │   ├── reviewController.js
│   │   ├── promoCodeController.js
│   │   ├── adminController.js
│   │   ├── menuItemController.js
│   │   ├── categoryController.js
│   │   ├── zoneController.js
│   │   └── uploadController.js
│   │
│   ├── routes/           # API routes (14 route files)
│   │   ├── index.js
│   │   ├── authRoutes.js
│   │   ├── userRoutes.js
│   │   ├── restaurantRoutes.js
│   │   ├── orderRoutes.js
│   │   ├── riderRoutes.js
│   │   ├── paymentRoutes.js
│   │   ├── reviewRoutes.js
│   │   ├── promoCodeRoutes.js
│   │   ├── adminRoutes.js
│   │   ├── menuItemRoutes.js
│   │   ├── categoryRoutes.js
│   │   ├── zoneRoutes.js
│   │   └── uploadRoutes.js
│   │
│   ├── middleware/       # Custom middleware
│   │   ├── auth.js       # JWT authentication
│   │   ├── validator.js  # Input validation
│   │   ├── errorHandler.js
│   │   ├── rateLimiter.js
│   │   └── upload.js     # File upload (Multer)
│   │
│   ├── services/         # External services
│   │   ├── bkashService.js
│   │   ├── nagadService.js
│   │   ├── sslCommerzService.js
│   │   ├── otpService.js
│   │   └── notificationService.js
│   │
│   ├── utils/            # Utility functions
│   │   ├── constants.js
│   │   ├── bdtFormatter.js
│   │   └── responseHelper.js
│   │
│   ├── app.js            # Express app setup
│   └── server.js         # Server entry point
│
├── uploads/              # Uploaded files
├── .env                  # Environment variables
├── .env.example          # Environment template
├── package.json
└── API_DOCUMENTATION.md
```

## 🔐 Security Features

- **JWT Authentication** with access and refresh tokens
- **Rate Limiting** on sensitive endpoints
- **Input Validation** with express-validator
- **CORS Configuration** for allowed origins
- **Helmet.js** for security headers
- **bcrypt** for password hashing
- **MongoDB Injection Protection**

## 📊 Database Schema

### Collections
1. **users** - User accounts (customers, restaurants, riders, admins)
2. **restaurants** - Restaurant information
3. **menuitems** - Menu items with customizations
4. **orders** - Order history and tracking
5. **riders** - Delivery personnel
6. **payments** - Payment transactions
7. **reviews** - Restaurant and rider reviews
8. **promocodes** - Discount codes
9. **categories** - Menu categories
10. **zones** - Delivery zones

### Indexes
- Users: phone (unique), email (unique)
- Restaurants: location (2dsphere), name (text)
- Orders: userId, restaurantId, riderId, status
- Riders: currentLocation (2dsphere)

## 🌐 WebSocket Namespaces

### `/order` - Order Tracking
- `subscribe` - Join order room
- `order:status` - Status updates
- `rider:location` - Live location

### `/rider` - Rider Updates
- `location` - Update location
- `order:new` - New order notification

### `/restaurant` - Restaurant Updates
- `subscribe` - Join restaurant room
- `order:new` - New order alert

### `/admin` - Admin Monitoring
- `subscribe` - Join admin channel
- `order:created` - System-wide order alerts

## 🚀 Deployment

### Using Docker

```bash
# Build image
docker build -t khabarlagbe-backend .

# Run container
docker run -d -p 5000:5000 \
  --env-file .env \
  --name khabarlagbe-api \
  khabarlagbe-backend
```

### Using PM2

```bash
# Install PM2
npm install -g pm2

# Start app
pm2 start src/server.js --name khabarlagbe-api

# Start with cluster mode
pm2 start src/server.js -i max --name khabarlagbe-api
```

### Environment Variables for Production

```bash
NODE_ENV=production
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/khabarlagbe
REDIS_URL=redis://prod-redis:6379
CORS_ORIGIN=https://khabarlagbe.com,https://admin.khabarlagbe.com
```

## 📈 Monitoring

### Health Check Endpoint
```bash
curl http://localhost:5000/health
```

### API Metrics
```bash
curl http://localhost:5000/api/v1
```

## 🐛 Debugging

### Enable Debug Logs
```bash
DEBUG=* npm run dev
```

### Check Database Connection
```bash
node -e "require('./src/config/database').connectDB().then(() => console.log('✅ Connected')).catch(e => console.error('❌', e))"
```

## 📝 Development Guidelines

### Code Style
- Use ES6+ features
- Follow Airbnb JavaScript Style Guide
- Use async/await over callbacks
- Handle all errors properly

### Git Workflow
```bash
# Feature branch
git checkout -b feature/your-feature

# Commit with clear messages
git commit -m "Add rider location tracking feature"

# Push and create PR
git push origin feature/your-feature
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📄 License

MIT License - see LICENSE file

## 💬 Support

- **Email**: dev@khabarlagbe.com
- **Issues**: https://github.com/noor-87dsdp-beep/KhabarLagbe/issues
- **Docs**: https://docs.khabarlagbe.com

## 🎯 Roadmap

- [ ] GraphQL API
- [ ] Microservices Architecture
- [ ] Kubernetes Deployment
- [ ] Advanced Analytics Dashboard
- [ ] Machine Learning Recommendations
- [ ] Multi-language Support

---

**Built with ❤️ for Bangladesh** 🇧🇩
