const axios = require('axios');

class SMSService {
  constructor() {
    // Bangladesh SMS Provider configurations
    this.provider = process.env.SMS_PROVIDER || 'ssl'; // ssl, mimsms, banglalink
    this.apiKey = process.env.SMS_API_KEY;
    this.senderId = process.env.SMS_SENDER_ID || 'KhabarLagbe';
  }

  // Generic SMS sending function
  async sendSMS(phone, message) {
    try {
      // Remove country code for local format if needed
      const localPhone = phone.replace('+880', '0');

      if (process.env.NODE_ENV === 'development') {
        console.log(`📱 SMS to ${phone}: ${message}`);
        return { success: true, messageId: 'dev-' + Date.now() };
      }

      let result;
      switch (this.provider) {
        case 'ssl':
          result = await this.sendViaSSL(localPhone, message);
          break;
        case 'mimsms':
          result = await this.sendViaMimSMS(localPhone, message);
          break;
        case 'banglalink':
          result = await this.sendViaBanglalink(localPhone, message);
          break;
        default:
          console.log(`📱 SMS (mock) to ${phone}: ${message}`);
          result = { success: true, messageId: 'mock-' + Date.now() };
      }

      return result;
    } catch (error) {
      console.error('❌ SMS error:', error.message);
      return { success: false, error: error.message };
    }
  }

  // SSL Wireless SMS Gateway
  async sendViaSSL(phone, message) {
    try {
      const response = await axios.get('https://sms.sslwireless.com/pushapi/dynamic/server.php', {
        params: {
          user: process.env.SSL_SMS_USER,
          pass: process.env.SSL_SMS_PASSWORD,
          sid: this.senderId,
          msisdn: phone,
          sms: message,
          csmsid: Date.now().toString(),
        },
      });

      if (response.data.includes('DONE')) {
        return { success: true, messageId: Date.now().toString() };
      }
      return { success: false, error: response.data };
    } catch (error) {
      throw error;
    }
  }

  // MIM SMS Gateway
  async sendViaMimSMS(phone, message) {
    try {
      const response = await axios.post('https://app.mimsms.com/smsAPI', {
        api_key: this.apiKey,
        type: 'text',
        contacts: phone,
        senderid: this.senderId,
        msg: message,
      });

      if (response.data.error === 0) {
        return { success: true, messageId: response.data.data.smsid };
      }
      return { success: false, error: response.data.msg };
    } catch (error) {
      throw error;
    }
  }

  // Banglalink SMS Gateway
  async sendViaBanglalink(phone, message) {
    try {
      const response = await axios.post('https://vas.banglalink.net/sms/send', {
        username: process.env.BANGLALINK_USER,
        password: process.env.BANGLALINK_PASSWORD,
        msisdn: phone,
        sender: this.senderId,
        message: message,
      }, {
        headers: { 'Content-Type': 'application/json' },
      });

      if (response.data.status === 'success') {
        return { success: true, messageId: response.data.messageId };
      }
      return { success: false, error: response.data.message };
    } catch (error) {
      throw error;
    }
  }

  // Template: OTP Message
  async sendOTP(phone, otp) {
    const message = `Your KhabarLagbe verification code is: ${otp}. Valid for ${process.env.OTP_EXPIRY_MINUTES || 10} minutes. Do not share with anyone.`;
    return this.sendSMS(phone, message);
  }

  // Template: Order Confirmation
  async sendOrderConfirmation(phone, orderNumber, total) {
    const message = `KhabarLagbe: Order #${orderNumber} confirmed! Total: ৳${(total / 100).toFixed(0)}. Track in app.`;
    return this.sendSMS(phone, message);
  }

  // Template: Order Status Update
  async sendOrderStatusUpdate(phone, orderNumber, status) {
    const statusMessages = {
      preparing: 'Your food is being prepared',
      ready: 'Your order is ready for pickup',
      picked_up: 'Rider has picked up your order',
      on_the_way: 'Your food is on the way',
      delivered: 'Order delivered! Enjoy your meal',
    };

    const message = `KhabarLagbe: Order #${orderNumber} - ${statusMessages[status] || status}`;
    return this.sendSMS(phone, message);
  }

  // Template: Rider Assignment
  async sendRiderAssigned(phone, orderNumber, riderName, riderPhone) {
    const message = `KhabarLagbe: ${riderName} is delivering your order #${orderNumber}. Contact: ${riderPhone}`;
    return this.sendSMS(phone, message);
  }

  // Template: New Order for Restaurant
  async sendNewOrderToRestaurant(phone, orderNumber, itemCount) {
    const message = `KhabarLagbe: New order #${orderNumber} received! ${itemCount} item(s). Please confirm in app.`;
    return this.sendSMS(phone, message);
  }

  // Template: Order Available for Rider
  async sendOrderAvailableToRider(phone, restaurantName, earnings) {
    const message = `KhabarLagbe: New delivery available from ${restaurantName}. Earn ৳${earnings}. Accept now!`;
    return this.sendSMS(phone, message);
  }

  // Template: Promotional Message
  async sendPromotion(phone, message) {
    return this.sendSMS(phone, `KhabarLagbe: ${message}`);
  }
}

module.exports = new SMSService();
