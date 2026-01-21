const nodemailer = require('nodemailer');

class EmailService {
  constructor() {
    this.transporter = null;
    this.initTransporter();
  }

  initTransporter() {
    // Use SMTP configuration from environment
    if (process.env.SMTP_HOST) {
      this.transporter = nodemailer.createTransport({
        host: process.env.SMTP_HOST,
        port: parseInt(process.env.SMTP_PORT || '587'),
        secure: process.env.SMTP_SECURE === 'true',
        auth: {
          user: process.env.SMTP_USER,
          pass: process.env.SMTP_PASSWORD,
        },
      });
    } else {
      console.warn('⚠️ SMTP not configured, email service disabled');
    }
  }

  async sendEmail(to, subject, html, text = null) {
    try {
      if (!this.transporter) {
        console.log(`📧 Email to ${to}: ${subject}`);
        return { success: true, messageId: 'mock-' + Date.now() };
      }

      const info = await this.transporter.sendMail({
        from: process.env.SMTP_FROM || '"KhabarLagbe" <noreply@khabarlagbe.com>',
        to,
        subject,
        text: text || subject,
        html,
      });

      console.log(`✅ Email sent: ${info.messageId}`);
      return { success: true, messageId: info.messageId };
    } catch (error) {
      console.error('❌ Email error:', error.message);
      return { success: false, error: error.message };
    }
  }

  // Order confirmation email
  async sendOrderConfirmation(email, order, restaurant) {
    const subject = `Order Confirmed - #${order.orderNumber}`;
    const html = `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2 style="color: #4CAF50;">🎉 Your Order is Confirmed!</h2>
        <p>Thank you for ordering with KhabarLagbe.</p>
        
        <div style="background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h3 style="margin-top: 0;">Order Details</h3>
          <p><strong>Order Number:</strong> ${order.orderNumber}</p>
          <p><strong>Restaurant:</strong> ${restaurant.name}</p>
          <p><strong>Total:</strong> ৳${(order.total / 100).toFixed(2)}</p>
          <p><strong>Estimated Delivery:</strong> ${order.estimatedDelivery || '30-45 minutes'}</p>
        </div>
        
        <p>Track your order in the KhabarLagbe app!</p>
        
        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd;">
          <p style="color: #666; font-size: 12px;">
            KhabarLagbe - Fresh Food, Fast Delivery<br>
            Questions? Contact us at support@khabarlagbe.com
          </p>
        </div>
      </div>
    `;

    return this.sendEmail(email, subject, html);
  }

  // Order status update email
  async sendOrderStatusUpdate(email, order, status) {
    const statusMessages = {
      preparing: '👨‍🍳 Your food is being prepared!',
      ready: '✅ Your order is ready!',
      picked_up: '🏍️ Rider has picked up your order!',
      on_the_way: '🚀 Your order is on the way!',
      delivered: '🎉 Order delivered! Enjoy your meal!',
      cancelled: '❌ Your order has been cancelled',
    };

    const subject = `Order Update - #${order.orderNumber}`;
    const html = `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>${statusMessages[status] || 'Order Update'}</h2>
        <p>Your order #${order.orderNumber} status has been updated.</p>
        <p><strong>New Status:</strong> ${status.replace('_', ' ').toUpperCase()}</p>
        
        <div style="margin-top: 30px;">
          <p style="color: #666; font-size: 12px;">
            KhabarLagbe - Fresh Food, Fast Delivery
          </p>
        </div>
      </div>
    `;

    return this.sendEmail(email, subject, html);
  }

  // Welcome email for new users
  async sendWelcomeEmail(email, name) {
    const subject = 'Welcome to KhabarLagbe! 🍛';
    const html = `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2 style="color: #4CAF50;">Welcome to KhabarLagbe, ${name}! 🎉</h2>
        
        <p>We're excited to have you join Bangladesh's favorite food delivery platform.</p>
        
        <div style="background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
          <h3>What you can do:</h3>
          <ul>
            <li>🍽️ Order from 1000+ restaurants</li>
            <li>🚀 Get fast delivery to your doorstep</li>
            <li>💰 Pay with bKash, Nagad, or Cash</li>
            <li>⭐ Earn rewards with every order</li>
          </ul>
        </div>
        
        <p>Start exploring delicious food now!</p>
        
        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd;">
          <p style="color: #666; font-size: 12px;">
            KhabarLagbe - Fresh Food, Fast Delivery<br>
            Contact: support@khabarlagbe.com
          </p>
        </div>
      </div>
    `;

    return this.sendEmail(email, subject, html);
  }

  // Restaurant approval email
  async sendRestaurantApproval(email, restaurantName, approved, reason = null) {
    const subject = approved
      ? `🎉 ${restaurantName} is now live on KhabarLagbe!`
      : `Restaurant Application Update - ${restaurantName}`;

    const html = approved
      ? `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #4CAF50;">Congratulations! 🎉</h2>
          <p>Your restaurant <strong>${restaurantName}</strong> has been approved and is now live on KhabarLagbe!</p>
          <p>You can now start receiving orders from customers.</p>
          
          <div style="background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0;">
            <h3>Next Steps:</h3>
            <ul>
              <li>Complete your menu setup</li>
              <li>Set your business hours</li>
              <li>Start accepting orders!</li>
            </ul>
          </div>
        </div>
      `
      : `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2>Application Update</h2>
          <p>Unfortunately, your restaurant <strong>${restaurantName}</strong> application was not approved.</p>
          ${reason ? `<p><strong>Reason:</strong> ${reason}</p>` : ''}
          <p>Please review and resubmit your application.</p>
        </div>
      `;

    return this.sendEmail(email, subject, html);
  }

  // Password reset email
  async sendPasswordReset(email, resetToken) {
    const resetUrl = `${process.env.FRONTEND_URL}/reset-password?token=${resetToken}`;
    const subject = 'Password Reset Request - KhabarLagbe';
    const html = `
      <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>Password Reset Request</h2>
        <p>You requested a password reset for your KhabarLagbe account.</p>
        
        <p>Click the button below to reset your password:</p>
        
        <div style="text-align: center; margin: 30px 0;">
          <a href="${resetUrl}" 
             style="background: #4CAF50; color: white; padding: 15px 30px; 
                    text-decoration: none; border-radius: 5px; display: inline-block;">
            Reset Password
          </a>
        </div>
        
        <p style="color: #666; font-size: 12px;">
          This link expires in 1 hour. If you didn't request this, please ignore this email.
        </p>
      </div>
    `;

    return this.sendEmail(email, subject, html);
  }
}

module.exports = new EmailService();
