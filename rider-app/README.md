# Rider App - KhabarLagbe Delivery

## Overview
The Rider App is designed for delivery personnel to manage orders, navigate efficiently, and track earnings.

## Features

### Authentication
- Rider-specific login with phone number
- Document verification (license, vehicle registration)
- Profile with vehicle details

### Order Management
- View available delivery requests in real-time
- Accept/reject orders with reasons
- View order details (pickup location, delivery address, items)
- Navigation to restaurant using Mapbox
- Navigation to customer using Mapbox
- Mark order status:
  - Arrived at restaurant
  - Order picked up
  - On the way to customer
  - Order delivered

### Live Location Tracking
- Background location tracking service
- Share live location with customers
- Auto-update location every 10 seconds
- Battery-optimized tracking

### Navigation
- Turn-by-turn directions using Mapbox
- Optimized routes considering traffic
- Alternative route suggestions
- Voice-guided navigation
- ETA calculations

### Earnings Dashboard
- Today's earnings
- Weekly/monthly earnings summary
- Order history with payment details
- Tips received
- Bonuses and incentives
- Payout management

### Communication
- In-app chat with customers
- In-app chat with restaurant
- Call customer/restaurant
- Emergency support button

## Tech Stack
- Android (Kotlin)
- Jetpack Compose for UI
- Mapbox for navigation and maps
- Firebase for real-time data
- Socket.IO for live updates
- Background location services
- WorkManager for persistent tasks

## UI Screens
1. Login/Register
2. Dashboard (available orders)
3. Order Details
4. Navigation Map
5. Order Status Update
6. Earnings
7. Profile
8. Help & Support

## Key Components
- Location tracking service
- Order notification receiver
- Map with route overlay
- Real-time order updates
- Payment calculator
