const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const crypto = require('crypto');

class AuthService {
  constructor() {
    this.jwtSecret = process.env.JWT_SECRET || 'khabarlagbe-secret-key';
    this.refreshSecret = process.env.REFRESH_TOKEN_SECRET || 'khabarlagbe-refresh-secret';
    this.jwtExpiry = process.env.JWT_EXPIRES_IN || '15m';
    this.refreshExpiry = process.env.REFRESH_TOKEN_EXPIRES_IN || '7d';
  }

  // Generate JWT access token
  generateAccessToken(payload) {
    return jwt.sign(payload, this.jwtSecret, {
      expiresIn: this.jwtExpiry,
    });
  }

  // Generate refresh token
  generateRefreshToken(payload) {
    return jwt.sign(payload, this.refreshSecret, {
      expiresIn: this.refreshExpiry,
    });
  }

  // Generate both tokens
  generateTokenPair(userId, additionalPayload = {}) {
    const payload = { userId, ...additionalPayload };
    
    return {
      accessToken: this.generateAccessToken(payload),
      refreshToken: this.generateRefreshToken(payload),
    };
  }

  // Generate restaurant auth token
  generateRestaurantToken(restaurantId) {
    return {
      accessToken: this.generateAccessToken({ restaurantId, role: 'restaurant' }),
      refreshToken: this.generateRefreshToken({ restaurantId, role: 'restaurant' }),
    };
  }

  // Generate rider auth token
  generateRiderToken(riderId) {
    return {
      accessToken: this.generateAccessToken({ riderId, role: 'rider' }),
      refreshToken: this.generateRefreshToken({ riderId, role: 'rider' }),
    };
  }

  // Generate admin auth token
  generateAdminToken(userId) {
    return {
      accessToken: this.generateAccessToken({ userId, role: 'admin' }),
      refreshToken: this.generateRefreshToken({ userId, role: 'admin' }),
    };
  }

  // Verify access token
  verifyAccessToken(token) {
    try {
      return jwt.verify(token, this.jwtSecret);
    } catch (error) {
      throw new Error('Invalid or expired access token');
    }
  }

  // Verify refresh token
  verifyRefreshToken(token) {
    try {
      return jwt.verify(token, this.refreshSecret);
    } catch (error) {
      throw new Error('Invalid or expired refresh token');
    }
  }

  // Refresh tokens
  refreshTokens(refreshToken) {
    const decoded = this.verifyRefreshToken(refreshToken);
    
    // Extract payload without exp and iat
    const { exp, iat, ...payload } = decoded;
    
    return this.generateTokenPair(payload.userId, payload);
  }

  // Hash password
  async hashPassword(password) {
    const salt = await bcrypt.genSalt(12);
    return bcrypt.hash(password, salt);
  }

  // Compare password
  async comparePassword(password, hashedPassword) {
    return bcrypt.compare(password, hashedPassword);
  }

  // Generate password reset token
  generatePasswordResetToken() {
    const resetToken = crypto.randomBytes(32).toString('hex');
    const hashedToken = crypto
      .createHash('sha256')
      .update(resetToken)
      .digest('hex');
    
    return {
      resetToken,
      hashedToken,
      expiresAt: new Date(Date.now() + 60 * 60 * 1000), // 1 hour
    };
  }

  // Verify password reset token
  verifyPasswordResetToken(token) {
    return crypto
      .createHash('sha256')
      .update(token)
      .digest('hex');
  }

  // Generate email verification token
  generateEmailVerificationToken() {
    const token = crypto.randomBytes(32).toString('hex');
    const hashedToken = crypto
      .createHash('sha256')
      .update(token)
      .digest('hex');
    
    return {
      token,
      hashedToken,
      expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000), // 24 hours
    };
  }

  // Generate API key for external integrations
  generateApiKey() {
    return `kl_${crypto.randomBytes(32).toString('hex')}`;
  }

  // Hash API key for storage
  hashApiKey(apiKey) {
    return crypto
      .createHash('sha256')
      .update(apiKey)
      .digest('hex');
  }

  // Extract token from authorization header
  extractTokenFromHeader(authHeader) {
    if (!authHeader) return null;
    
    const parts = authHeader.split(' ');
    if (parts.length !== 2 || parts[0] !== 'Bearer') {
      return null;
    }
    
    return parts[1];
  }

  // Decode token without verification (for debugging)
  decodeToken(token) {
    return jwt.decode(token);
  }

  // Check if token is expired
  isTokenExpired(token) {
    try {
      const decoded = jwt.decode(token);
      if (!decoded || !decoded.exp) return true;
      return Date.now() >= decoded.exp * 1000;
    } catch {
      return true;
    }
  }

  // Get token expiry time
  getTokenExpiry(token) {
    const decoded = jwt.decode(token);
    if (!decoded || !decoded.exp) return null;
    return new Date(decoded.exp * 1000);
  }
}

module.exports = new AuthService();
