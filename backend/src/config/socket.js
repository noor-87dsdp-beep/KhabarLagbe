const { Server } = require('socket.io');

let io;

const setupSocket = (server) => {
  io = new Server(server, {
    cors: {
      origin: process.env.CORS_ORIGIN || '*',
      methods: ['GET', 'POST'],
    },
  });

  // Order tracking namespace
  const orderNamespace = io.of('/order');
  
  orderNamespace.on('connection', (socket) => {
    console.log(`📱 Client connected to order tracking: ${socket.id}`);

    // Subscribe to order updates
    socket.on('subscribe', (orderId) => {
      socket.join(`order:${orderId}`);
      console.log(`👁️ Client ${socket.id} subscribed to order ${orderId}`);
    });

    // Unsubscribe from order updates
    socket.on('unsubscribe', (orderId) => {
      socket.leave(`order:${orderId}`);
      console.log(`👋 Client ${socket.id} unsubscribed from order ${orderId}`);
    });

    socket.on('disconnect', () => {
      console.log(`📱 Client disconnected: ${socket.id}`);
    });
  });

  // Rider location namespace
  const riderNamespace = io.of('/rider');
  
  riderNamespace.on('connection', (socket) => {
    console.log(`🏍️ Rider connected: ${socket.id}`);

    // Rider location update
    socket.on('location', (data) => {
      const { riderId, lat, lng, orderId } = data;
      // Broadcast to order tracking
      orderNamespace.to(`order:${orderId}`).emit('rider:location', {
        riderId,
        lat,
        lng,
        timestamp: new Date(),
      });
    });

    socket.on('disconnect', () => {
      console.log(`🏍️ Rider disconnected: ${socket.id}`);
    });
  });

  console.log('✅ Socket.IO configured');
  return io;
};

const getIO = () => {
  if (!io) {
    throw new Error('Socket.IO not initialized');
  }
  return io;
};

module.exports = setupSocket;
module.exports.getIO = getIO;
