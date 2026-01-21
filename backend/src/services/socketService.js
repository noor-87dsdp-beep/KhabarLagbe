const { getIO } = require('../config/socket');

class SocketService {
  constructor() {
    this.io = null;
  }

  getIO() {
    if (!this.io) {
      try {
        this.io = getIO();
      } catch (error) {
        console.warn('Socket.IO not initialized:', error.message);
        return null;
      }
    }
    return this.io;
  }

  // Order Events

  // Emit new order to restaurant
  emitNewOrder(restaurantId, order) {
    const io = this.getIO();
    if (!io) return;

    io.of('/restaurant')
      .to(`restaurant:${restaurantId}`)
      .emit('order:new', {
        orderId: order._id,
        orderNumber: order.orderNumber,
        items: order.items.length,
        total: order.total,
        paymentMethod: order.paymentMethod,
        timestamp: new Date(),
      });

    // Also emit to admin
    io.of('/admin')
      .emit('order:new', {
        orderId: order._id,
        orderNumber: order.orderNumber,
        restaurantId,
      });
  }

  // Emit order status update
  emitOrderStatusUpdate(orderId, status, additionalData = {}) {
    const io = this.getIO();
    if (!io) return;

    io.of('/order')
      .to(`order:${orderId}`)
      .emit('order:status_update', {
        orderId,
        status,
        timestamp: new Date(),
        ...additionalData,
      });
  }

  // Emit order accepted by restaurant
  emitOrderAccepted(orderId, estimatedTime) {
    const io = this.getIO();
    if (!io) return;

    io.of('/order')
      .to(`order:${orderId}`)
      .emit('order:accepted', {
        orderId,
        status: 'confirmed',
        estimatedTime,
        timestamp: new Date(),
      });
  }

  // Emit rider assigned to order
  emitRiderAssigned(orderId, rider) {
    const io = this.getIO();
    if (!io) return;

    io.of('/order')
      .to(`order:${orderId}`)
      .emit('rider:assigned', {
        orderId,
        rider: {
          id: rider._id,
          name: rider.name,
          phone: rider.phone,
          profileImage: rider.profileImage,
          rating: rider.rating,
        },
        timestamp: new Date(),
      });
  }

  // Rider Events

  // Emit rider location update
  emitRiderLocation(orderId, riderId, location) {
    const io = this.getIO();
    if (!io) return;

    io.of('/order')
      .to(`order:${orderId}`)
      .emit('rider:location_update', {
        orderId,
        riderId,
        location: {
          lat: location.lat,
          lng: location.lng,
        },
        timestamp: new Date(),
      });
  }

  // Emit rider status change
  emitRiderStatusChange(riderId, status) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .emit('rider:status_change', {
        riderId,
        status,
        timestamp: new Date(),
      });
  }

  // Emit new order available for riders
  emitNewOrderAvailable(order, restaurantLocation) {
    const io = this.getIO();
    if (!io) return;

    io.of('/rider')
      .emit('order:available', {
        orderId: order._id,
        orderNumber: order.orderNumber,
        restaurantLocation,
        deliveryLocation: order.deliveryAddress.location.coordinates,
        estimatedEarnings: order.deliveryFee,
        timestamp: new Date(),
      });
  }

  // Restaurant Events

  // Emit restaurant online/offline status
  emitRestaurantStatus(restaurantId, isOpen) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .emit('restaurant:status_change', {
        restaurantId,
        isOpen,
        timestamp: new Date(),
      });
  }

  // Admin Events

  // Emit real-time dashboard update
  emitDashboardUpdate(stats) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .to('dashboard')
      .emit('dashboard:update', {
        stats,
        timestamp: new Date(),
      });
  }

  // Emit new user registration
  emitNewUserRegistration(user) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .emit('user:new', {
        userId: user._id,
        phone: user.phone,
        timestamp: new Date(),
      });
  }

  // Emit new restaurant registration
  emitNewRestaurantRegistration(restaurant) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .emit('restaurant:new', {
        restaurantId: restaurant._id,
        name: restaurant.name,
        phone: restaurant.phone,
        timestamp: new Date(),
      });
  }

  // Emit new rider registration
  emitNewRiderRegistration(rider) {
    const io = this.getIO();
    if (!io) return;

    io.of('/admin')
      .emit('rider:new', {
        riderId: rider._id,
        name: rider.name,
        phone: rider.phone,
        zone: rider.zone,
        timestamp: new Date(),
      });
  }

  // Payment Events

  // Emit payment status update
  emitPaymentUpdate(orderId, paymentStatus, paymentMethod) {
    const io = this.getIO();
    if (!io) return;

    io.of('/order')
      .to(`order:${orderId}`)
      .emit('payment:update', {
        orderId,
        status: paymentStatus,
        method: paymentMethod,
        timestamp: new Date(),
      });
  }

  // Chat Events (for future implementation)

  // Emit chat message
  emitChatMessage(roomId, message) {
    const io = this.getIO();
    if (!io) return;

    io.of('/chat')
      .to(roomId)
      .emit('message:new', {
        ...message,
        timestamp: new Date(),
      });
  }

  // Emit typing indicator
  emitTypingIndicator(roomId, userId, isTyping) {
    const io = this.getIO();
    if (!io) return;

    io.of('/chat')
      .to(roomId)
      .emit('typing', {
        userId,
        isTyping,
      });
  }

  // Utility Methods

  // Join room
  joinRoom(socket, roomId) {
    socket.join(roomId);
    console.log(`Socket ${socket.id} joined room ${roomId}`);
  }

  // Leave room
  leaveRoom(socket, roomId) {
    socket.leave(roomId);
    console.log(`Socket ${socket.id} left room ${roomId}`);
  }

  // Broadcast to all connected clients
  broadcastToAll(event, data) {
    const io = this.getIO();
    if (!io) return;

    io.emit(event, {
      ...data,
      timestamp: new Date(),
    });
  }

  // Get connected clients count
  async getConnectedClientsCount(namespace = '/') {
    const io = this.getIO();
    if (!io) return 0;

    const sockets = await io.of(namespace).fetchSockets();
    return sockets.length;
  }
}

module.exports = new SocketService();
