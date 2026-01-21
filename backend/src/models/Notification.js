const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
  },
  restaurant: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Restaurant',
  },
  rider: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Rider',
  },
  type: {
    type: String,
    enum: [
      'order_placed',
      'order_confirmed',
      'order_preparing',
      'order_ready',
      'order_picked_up',
      'order_delivered',
      'order_cancelled',
      'rider_assigned',
      'payment_success',
      'payment_failed',
      'promo_code',
      'system',
      'marketing',
    ],
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  titleBn: String,
  body: {
    type: String,
    required: true,
  },
  bodyBn: String,
  image: String,
  data: {
    orderId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Order',
    },
    restaurantId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Restaurant',
    },
    promoCode: String,
    actionUrl: String,
    custom: mongoose.Schema.Types.Mixed,
  },
  channel: {
    type: String,
    enum: ['push', 'sms', 'email', 'in_app'],
    default: 'push',
  },
  status: {
    type: String,
    enum: ['pending', 'sent', 'delivered', 'failed', 'read'],
    default: 'pending',
  },
  sentAt: Date,
  readAt: Date,
  fcmMessageId: String,
  errorMessage: String,
}, {
  timestamps: true,
});

// Indexes for efficient queries
notificationSchema.index({ user: 1, createdAt: -1 });
notificationSchema.index({ restaurant: 1, createdAt: -1 });
notificationSchema.index({ rider: 1, createdAt: -1 });
notificationSchema.index({ status: 1 });
notificationSchema.index({ type: 1 });

// Virtual for checking if notification is unread
notificationSchema.virtual('isUnread').get(function() {
  return this.status !== 'read';
});

// Static method to get unread count
notificationSchema.statics.getUnreadCount = async function(userId) {
  return this.countDocuments({
    user: userId,
    status: { $ne: 'read' },
  });
};

// Static method to mark all as read
notificationSchema.statics.markAllAsRead = async function(userId) {
  return this.updateMany(
    { user: userId, status: { $ne: 'read' } },
    { status: 'read', readAt: new Date() }
  );
};

module.exports = mongoose.model('Notification', notificationSchema);
