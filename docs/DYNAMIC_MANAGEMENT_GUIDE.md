# 🎯 Dynamic Management Implementation - Complete Guide

This guide provides everything you need to know about the dynamic management implementation in KhabarLagbe.

## 📋 Quick Links

- [Mapbox Setup Guide](./MAPBOX_SETUP.md) - How to configure Mapbox
- [Implementation Details](./IMPLEMENTATION_DETAILS.md) - Technical documentation
- [API Reference](#api-reference) - Complete API documentation
- [Database Schema](#database-schema) - Data models

## 🌟 What's New

### Zero Hardcoded Data
Everything in KhabarLagbe is now **100% dynamic**:
- ✅ Delivery zones controlled from admin panel
- ✅ Restaurants manage their own menus
- ✅ Real-time updates across all apps
- ✅ Mapbox integration for all location features

### For Administrators
- Complete platform control via web interface
- Zone management with Mapbox polygons
- Restaurant approval workflow
- Real-time monitoring capability

### For Restaurant Owners
- Self-service registration and onboarding
- Complete menu management (categories & items)
- Business hours configuration
- Analytics dashboard
- Profile and location management

### For Customers (Ready for Implementation)
- Dynamic delivery zones
- Real-time order tracking with Mapbox
- Accurate ETAs
- Live rider location

### For Riders (Ready for Implementation)
- Turn-by-turn navigation
- Route optimization
- Live location broadcasting
- Distance-based earnings

## 🚀 Getting Started

### Prerequisites
- Node.js 16+
- MongoDB 6+
- Android Studio (for mobile apps)
- Mapbox account ([sign up here](https://www.mapbox.com/))

### 1. Backend Setup

```bash
cd backend

# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Add your Mapbox tokens to .env
# MAPBOX_ACCESS_TOKEN=pk.your_token
# MAPBOX_SECRET_TOKEN=sk.your_token

# Start the server
npm run dev
```

Server will run on http://localhost:3000

### 2. Admin Panel Setup

```bash
cd admin-panel

# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Add your Mapbox token to .env
# VITE_MAPBOX_ACCESS_TOKEN=pk.your_token

# Start development server
npm run dev
```

Admin panel will run on http://localhost:5173

### 3. Android Apps Setup

See [Mapbox Setup Guide](./MAPBOX_SETUP.md) for detailed instructions.

```bash
# Copy local.properties template
cp local.properties.example local.properties

# Add your Mapbox tokens to local.properties
# MAPBOX_ACCESS_TOKEN=pk.your_token
# MAPBOX_DOWNLOADS_TOKEN=sk.your_token

# Build apps
./gradlew :app:assembleDebug
./gradlew :rider-app:assembleDebug
./gradlew :restaurant-app:assembleDebug
```

## 📡 API Reference

### Base URL
```
Development: http://localhost:3000/api/v1
Production: https://api.khabarlagbe.com/api/v1
```

### Authentication

#### Get Token
All authenticated requests require a JWT token in the Authorization header:
```http
Authorization: Bearer <your_jwt_token>
```

### Zone Management

#### Get Active Zones (Public)
```http
GET /zones
```

Response:
```json
{
  "success": true,
  "data": [
    {
      "_id": "zone123",
      "name": "Kashimpur",
      "nameBn": "কাশিমপুর",
      "deliveryFee": 3000,
      "perKmFee": 800,
      "estimatedTime": "30-45 min",
      "isActive": true,
      "center": {
        "type": "Point",
        "coordinates": [90.4152, 23.7461]
      }
    }
  ]
}
```

#### Create Zone (Admin)
```http
POST /zones
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Konabari",
  "nameBn": "কোনাবাড়ী",
  "polygon": {
    "type": "Polygon",
    "coordinates": [[[90.4, 23.7], [90.5, 23.7], [90.5, 23.8], [90.4, 23.8], [90.4, 23.7]]]
  },
  "center": {
    "type": "Point",
    "coordinates": [90.45, 23.75]
  },
  "deliveryFee": 3000,
  "perKmFee": 800,
  "estimatedTime": "30-45 min"
}
```

### Restaurant Management

#### Register Restaurant (Self-Service)
```http
POST /restaurants/register
Content-Type: application/json

{
  "name": "Spice Garden",
  "nameBn": "স্পাইস গার্ডেন",
  "phone": "+8801712345678",
  "email": "info@spicegarden.com",
  "location": {
    "type": "Point",
    "coordinates": [90.4152, 23.7461]
  },
  "address": {
    "street": "123 Main Road",
    "area": "Kashimpur",
    "thana": "Gazipur Sadar",
    "district": "Gazipur"
  },
  "cuisines": ["Bengali", "Indian"],
  "category": "Restaurant"
}
```

#### Update Restaurant Profile (Restaurant Owner)
```http
PUT /restaurants/profile
Authorization: Bearer <restaurant_token>
Content-Type: application/json

{
  "description": "Best Bengali food in town",
  "minOrderAmount": 15000,
  "deliveryRadius": 5
}
```

#### Approve Restaurant (Admin)
```http
PATCH /restaurants/admin/:id/approve
Authorization: Bearer <admin_token>
```

### Menu Management

#### Create Category (Restaurant Owner)
```http
POST /categories
Authorization: Bearer <restaurant_token>
Content-Type: application/json

{
  "name": "Main Course",
  "nameBn": "প্রধান খাবার",
  "description": "Traditional Bengali dishes",
  "order": 1
}
```

#### Create Menu Item (Restaurant Owner)
```http
POST /menu-items
Authorization: Bearer <restaurant_token>
Content-Type: application/json

{
  "name": "Beef Tehari",
  "nameBn": "গরুর তেহারি",
  "description": "Spiced rice with tender beef",
  "category": "category_id_here",
  "price": 25000,
  "prepTime": 25,
  "isVegetarian": false,
  "spiceLevel": "Medium"
}
```

#### Toggle Menu Item Availability (Restaurant Owner)
```http
PATCH /menu-items/:id/availability
Authorization: Bearer <restaurant_token>
```

### Analytics (Restaurant Owner)
```http
GET /restaurants/my/analytics?period=today
Authorization: Bearer <restaurant_token>
```

Response:
```json
{
  "success": true,
  "data": {
    "totalOrders": 45,
    "totalRevenue": 125000,
    "averageOrderValue": 2777,
    "period": "today"
  }
}
```

## 🗄️ Database Schema

### Zone Collection
```javascript
{
  _id: ObjectId,
  name: String,              // "Kashimpur"
  nameBn: String,            // "কাশিমপুর"
  polygon: {
    type: "Polygon",
    coordinates: [[[lng, lat], ...]]
  },
  center: {
    type: "Point",
    coordinates: [lng, lat]
  },
  deliveryFee: Number,       // in paisa (3000 = ৳30)
  perKmFee: Number,          // in paisa (800 = ৳8)
  estimatedTime: String,     // "30-45 min"
  isActive: Boolean,
  restaurantCount: Number,
  riderCount: Number,
  createdAt: Date,
  updatedAt: Date
}
```

### Category Collection
```javascript
{
  _id: ObjectId,
  restaurant: ObjectId,      // Reference to Restaurant
  name: String,              // "Main Course"
  nameBn: String,           // "প্রধান খাবার"
  description: String,
  descriptionBn: String,
  image: String,            // URL
  order: Number,            // For sorting
  isActive: Boolean,
  createdAt: Date,
  updatedAt: Date
}
```

### MenuItem Collection (Updated)
```javascript
{
  _id: ObjectId,
  restaurant: ObjectId,
  category: ObjectId,        // Changed from String to ObjectId
  categoryName: String,      // Denormalized
  name: String,
  nameBn: String,
  description: String,
  price: Number,             // in paisa
  discountPrice: Number,
  image: String,
  customizations: [{
    name: String,
    options: [{
      name: String,
      price: Number
    }],
    required: Boolean
  }],
  prepTime: Number,          // minutes
  isAvailable: Boolean,
  isPopular: Boolean,
  rating: Number,
  totalReviews: Number,
  totalOrders: Number
}
```

### Restaurant Collection (Updated)
```javascript
{
  _id: ObjectId,
  // ... existing fields ...
  deliveryRadius: Number,     // NEW: in km
  deliveryZones: [ObjectId],  // NEW: References to Zone
  documents: {                // NEW
    tradeLicense: String,
    nidOwner: String,
    bankDetails: String
  },
  payoutMethod: {            // NEW
    type: String,            // "bkash", "nagad", "bank"
    accountNumber: String,
    accountName: String,
    bankName: String,
    branchName: String
  },
  approvalStatus: String,    // NEW: "pending", "approved", "rejected"
  rejectionReason: String    // NEW
}
```

## 🔌 WebSocket Events

### Connect to Server
```javascript
import io from 'socket.io-client';

// Order tracking
const orderSocket = io('http://localhost:3000/order');

// Rider location
const riderSocket = io('http://localhost:3000/rider');

// Restaurant notifications
const restaurantSocket = io('http://localhost:3000/restaurant');

// Admin monitoring
const adminSocket = io('http://localhost:3000/admin');
```

### Subscribe to Order Updates (Customer)
```javascript
orderSocket.emit('subscribe', orderId);

orderSocket.on('rider:location', (data) => {
  console.log('Rider location:', data.lat, data.lng);
  console.log('ETA:', data.eta);
});
```

### Send Location Updates (Rider)
```javascript
riderSocket.emit('location', {
  riderId: 'rider123',
  lat: 23.7461,
  lng: 90.4152,
  orderId: 'order456'
});
```

### Subscribe to Orders (Restaurant)
```javascript
restaurantSocket.emit('subscribe', restaurantId);

restaurantSocket.on('order:new', (order) => {
  console.log('New order received:', order);
  // Play alert sound
  // Show notification
});
```

## 🎨 Admin Panel Usage

### Zone Management
1. Navigate to "জোন ব্যবস্থাপনা" (Zone Management)
2. View all zones with their details
3. Toggle zone active/inactive status
4. Delete zones (only if no restaurants in zone)
5. Click "Add New Zone" (map integration coming soon)

### Restaurant Management
1. Navigate to "রেস্টুরেন্ট" (Restaurants)
2. Switch between "All Restaurants" and "Pending Approval" tabs
3. For pending restaurants:
   - Click "Approve" to activate
   - Click "Reject" to deny (provide reason)
4. For approved restaurants:
   - Click "Suspend" to temporarily disable
   - Click "Activate" to enable again
5. View restaurant stats: rating, reviews, orders

## 🔧 Development Tips

### Testing APIs with curl

```bash
# Get active zones
curl http://localhost:3000/api/v1/zones

# Create zone (admin)
curl -X POST http://localhost:3000/api/v1/zones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_admin_token" \
  -d '{
    "name": "Test Zone",
    "nameBn": "টেস্ট জোন",
    "polygon": {...},
    "center": {...},
    "deliveryFee": 3000
  }'

# Register restaurant
curl -X POST http://localhost:3000/api/v1/restaurants/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Restaurant",
    "phone": "+8801700000000",
    "location": {...},
    "address": {...}
  }'
```

### Testing WebSocket

```javascript
// In browser console
const socket = io('http://localhost:3000/order');

socket.on('connect', () => {
  console.log('Connected to order namespace');
  socket.emit('subscribe', 'order123');
});

socket.on('rider:location', (data) => {
  console.log('Rider location update:', data);
});
```

## 📊 Monitoring & Logging

### Backend Logs
The backend logs all requests and errors:
```
✅ Database connected successfully
🚀 Server running on port 3000
📱 Client connected to order tracking: socket_id
🏍️ Rider connected: socket_id
🍽️ Restaurant connected: socket_id
```

### Admin Panel Logs
Check browser console for:
- API requests/responses
- WebSocket connection status
- Error messages

## 🐛 Troubleshooting

### "Cannot find zone"
- Ensure zone is created via admin panel or API
- Check zone `isActive` status

### "Restaurant not approved"
- Check restaurant `approvalStatus` in database
- Use admin panel to approve

### "Unauthorized" errors
- Verify JWT token is valid
- Check token has correct role (admin/restaurant/rider)

### Mapbox not loading
- Verify MAPBOX_ACCESS_TOKEN is set correctly
- Check token hasn't expired
- Ensure token has appropriate scopes

### Build errors (Android)
- Verify MAPBOX_DOWNLOADS_TOKEN is set
- Run `./gradlew clean`
- Check internet connection

## 📚 Additional Resources

- [Mapbox Documentation](https://docs.mapbox.com/)
- [Socket.IO Documentation](https://socket.io/docs/v4/)
- [MongoDB Geospatial Queries](https://docs.mongodb.com/manual/geospatial-queries/)
- [Express.js Documentation](https://expressjs.com/)
- [React Documentation](https://react.dev/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

## 🤝 Contributing

When contributing to this project:
1. Follow existing code structure
2. Add tests for new features
3. Update documentation
4. Ensure all APIs have proper error handling
5. Test with real Mapbox integration

## 📝 License

This project is licensed under the MIT License.

---

**Built with ❤️ for Bangladesh 🇧🇩**
