// Common helper functions for KhabarLagbe Backend

// Generate random string
const generateRandomString = (length = 32) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

// Generate order number
const generateOrderNumber = () => {
  const timestamp = Date.now().toString().slice(-8);
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  return `KL${timestamp}${random}`;
};

// Format price from paisa to Taka
const formatPrice = (paisa) => {
  const taka = paisa / 100;
  return `৳${taka.toLocaleString('en-BD', { minimumFractionDigits: 0, maximumFractionDigits: 2 })}`;
};

// Parse price from Taka to paisa
const parsePriceToPaisa = (taka) => {
  return Math.round(parseFloat(taka) * 100);
};

// Validate Bangladesh phone number
const isValidBDPhone = (phone) => {
  return /^\+880\d{10}$/.test(phone);
};

// Format phone number to Bangladesh format
const formatPhoneNumber = (phone) => {
  // Remove all non-digits
  let cleaned = phone.replace(/\D/g, '');
  
  // Add country code if not present
  if (cleaned.startsWith('0')) {
    cleaned = '880' + cleaned.slice(1);
  }
  
  if (!cleaned.startsWith('880')) {
    cleaned = '880' + cleaned;
  }
  
  return '+' + cleaned;
};

// Calculate percentage
const calculatePercentage = (value, total) => {
  if (total === 0) return 0;
  return Math.round((value / total) * 100 * 100) / 100;
};

// Sleep function for async operations
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Retry function for flaky operations
const retry = async (fn, retries = 3, delay = 1000) => {
  try {
    return await fn();
  } catch (error) {
    if (retries <= 0) throw error;
    await sleep(delay);
    return retry(fn, retries - 1, delay * 2);
  }
};

// Deep clone object
const deepClone = (obj) => JSON.parse(JSON.stringify(obj));

// Remove undefined values from object
const cleanObject = (obj) => {
  const cleaned = {};
  Object.keys(obj).forEach(key => {
    if (obj[key] !== undefined && obj[key] !== null) {
      cleaned[key] = obj[key];
    }
  });
  return cleaned;
};

// Capitalize first letter
const capitalize = (str) => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

// Slugify string
const slugify = (str) => {
  return str
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/[\s_-]+/g, '-')
    .replace(/^-+|-+$/g, '');
};

// Parse boolean from string
const parseBoolean = (value) => {
  if (typeof value === 'boolean') return value;
  if (typeof value === 'string') {
    return value.toLowerCase() === 'true' || value === '1';
  }
  return Boolean(value);
};

// Get current Bangladesh time
const getBangladeshTime = () => {
  const options = {
    timeZone: 'Asia/Dhaka',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  };
  return new Date().toLocaleString('en-GB', options);
};

// Check if restaurant is currently open
const isRestaurantOpen = (businessHours) => {
  if (!businessHours || businessHours.length === 0) return true;
  
  const now = new Date();
  const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
  const currentDay = days[now.getDay()];
  
  const todayHours = businessHours.find(h => h.day === currentDay);
  if (!todayHours || todayHours.isClosed) return false;
  
  const currentTime = now.getHours() * 100 + now.getMinutes();
  const [openHour, openMin] = todayHours.openTime.split(':').map(Number);
  const [closeHour, closeMin] = todayHours.closeTime.split(':').map(Number);
  
  const openTime = openHour * 100 + openMin;
  const closeTime = closeHour * 100 + closeMin;
  
  return currentTime >= openTime && currentTime <= closeTime;
};

// Calculate estimated delivery time
const calculateEstimatedDelivery = (prepTimeMinutes = 15, distanceKm = 5) => {
  const avgSpeedKmH = 25; // Average speed in Dhaka traffic
  const travelTime = Math.ceil((distanceKm / avgSpeedKmH) * 60);
  const totalMinutes = prepTimeMinutes + travelTime + 5; // 5 min buffer
  
  const estimatedTime = new Date();
  estimatedTime.setMinutes(estimatedTime.getMinutes() + totalMinutes);
  
  return {
    minutes: totalMinutes,
    time: estimatedTime,
    formatted: `${totalMinutes} min`,
  };
};

// Mask sensitive data
const maskPhone = (phone) => {
  if (!phone) return '';
  return phone.slice(0, 4) + '****' + phone.slice(-4);
};

const maskEmail = (email) => {
  if (!email) return '';
  const [name, domain] = email.split('@');
  return name.slice(0, 2) + '***@' + domain;
};

// Generate OTP
const generateOTP = (length = 6) => {
  let otp = '';
  for (let i = 0; i < length; i++) {
    otp += Math.floor(Math.random() * 10);
  }
  return otp;
};

// Validate MongoDB ObjectId
const isValidObjectId = (id) => {
  return /^[0-9a-fA-F]{24}$/.test(id);
};

// Sanitize user input
const sanitizeInput = (input) => {
  if (typeof input !== 'string') return input;
  return input
    .replace(/[<>]/g, '')
    .trim();
};

// Format date for Bangladesh locale
const formatDateBD = (date, options = {}) => {
  const defaultOptions = {
    timeZone: 'Asia/Dhaka',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    ...options,
  };
  return new Date(date).toLocaleDateString('bn-BD', defaultOptions);
};

module.exports = {
  generateRandomString,
  generateOrderNumber,
  formatPrice,
  parsePriceToPaisa,
  isValidBDPhone,
  formatPhoneNumber,
  calculatePercentage,
  sleep,
  retry,
  deepClone,
  cleanObject,
  capitalize,
  slugify,
  parseBoolean,
  getBangladeshTime,
  isRestaurantOpen,
  calculateEstimatedDelivery,
  maskPhone,
  maskEmail,
  generateOTP,
  isValidObjectId,
  sanitizeInput,
  formatDateBD,
};
