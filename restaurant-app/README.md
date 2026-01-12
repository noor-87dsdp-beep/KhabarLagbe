# Restaurant App - KhabarLagbe Business Dashboard

## Overview
The Restaurant App enables restaurant owners and managers to manage their menu, handle incoming orders, and track business performance.

## Features

### Menu Management
- Add new menu items with details:
  - Name, description, price
  - Category assignment
  - Dietary information (vegetarian, vegan, gluten-free)
  - Food images (multiple)
  - Customization options
  - Preparation time
- Edit existing items
- Delete items
- Toggle item availability (in stock/out of stock)
- Bulk operations for categories
- Item ordering/sorting

### Order Management
- Real-time incoming order notifications
- Sound and vibration alerts
- Order details view:
  - Customer information
  - Order items with customizations
  - Special instructions
  - Delivery address
  - Payment method and status
- Accept orders with preparation time estimate
- Reject orders with reason
- Mark orders as ready for pickup
- Order history and search

### Restaurant Profile
- Business information
- Operating hours (daily schedule)
- Holiday/closed dates
- Restaurant images (banner, logo, gallery)
- Location on map
- Delivery radius settings
- Minimum order amount
- Delivery fee configuration

### Analytics Dashboard
- Sales overview (daily/weekly/monthly)
- Popular items ranking
- Revenue graphs
- Order trends
- Peak hours analysis
- Customer ratings summary
- Reviews management
- Performance metrics

### Notifications
- New order alerts
- Customer messages
- Rider pickup notifications
- Low stock alerts
- Payment confirmations

## Tech Stack
- Android (Kotlin) or Web App (React)
- Jetpack Compose / React with Material UI
- Firebase Firestore for data
- Firebase Storage for images
- Socket.IO for real-time orders
- Charts library for analytics
- Image picker and cropper

## UI Screens
1. Login
2. Dashboard (orders overview)
3. Incoming Orders List
4. Order Details
5. Menu Management
6. Add/Edit Item
7. Analytics
8. Profile Settings
9. Business Hours
10. Notifications

## Key Components
- Order notification system
- Real-time order stream
- Menu CRUD operations
- Image upload and management
- Analytics charts
- Profile editor
