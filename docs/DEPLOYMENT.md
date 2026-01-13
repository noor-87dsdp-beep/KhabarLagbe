# KhabarLagbe Deployment Guide

This guide covers deployment procedures for all components of the KhabarLagbe platform.

## 📋 Table of Contents

1. [Backend API Deployment](#backend-api-deployment)
2. [Admin Panel Deployment](#admin-panel-deployment)
3. [Android Apps Deployment](#android-apps-deployment)
4. [iOS App Deployment](#ios-app-deployment)
5. [Environment Configuration](#environment-configuration)
6. [Monitoring & Maintenance](#monitoring--maintenance)

---

## 🖥️ Backend API Deployment

### Prerequisites
- Node.js 20+ installed
- MongoDB database (Atlas or self-hosted)
- Redis instance for caching
- Firebase project for push notifications
- Domain with SSL certificate

### Production Setup

1. **Prepare Environment**
```bash
cd backend
cp .env.example .env
```

2. **Configure Environment Variables**
```env
NODE_ENV=production
PORT=5000
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/khabarlagbe
REDIS_URL=redis://localhost:6379
JWT_SECRET=your-super-secret-jwt-key-change-this
JWT_EXPIRES_IN=7d

# Payment Gateways
BKASH_APP_KEY=your_bkash_app_key
BKASH_APP_SECRET=your_bkash_secret
NAGAD_MERCHANT_ID=your_nagad_merchant_id
SSLCOMMERZ_STORE_ID=your_sslcommerz_store_id
SSLCOMMERZ_STORE_PASSWORD=your_sslcommerz_password

# Firebase
FIREBASE_PROJECT_ID=your-firebase-project
FIREBASE_PRIVATE_KEY=your-firebase-private-key
FIREBASE_CLIENT_EMAIL=your-firebase-client-email

# Mapbox
MAPBOX_ACCESS_TOKEN=your_mapbox_token

# SMS Gateway (for OTP)
SMS_API_KEY=your_sms_gateway_key
SMS_SENDER_ID=KhabarLagbe
```

3. **Install Dependencies**
```bash
npm ci --production
```

4. **Build (if using TypeScript)**
```bash
npm run build
```

5. **Run with PM2**
```bash
npm install -g pm2
pm2 start src/server.js --name khabarlagbe-api
pm2 save
pm2 startup
```

### Docker Deployment

```bash
# Build image
docker build -t khabarlagbe-backend .

# Run container
docker run -d \
  --name khabarlagbe-api \
  -p 5000:5000 \
  --env-file .env \
  khabarlagbe-backend

# With Docker Compose
docker-compose up -d
```

### Cloud Platforms

#### Heroku
```bash
heroku create khabarlagbe-api
heroku addons:create mongolab:sandbox
heroku config:set NODE_ENV=production
git push heroku master
```

#### AWS EC2
1. Launch Ubuntu instance
2. Install Node.js, MongoDB, Redis
3. Clone repository
4. Configure nginx as reverse proxy
5. Setup SSL with Let's Encrypt
6. Use PM2 for process management

#### DigitalOcean App Platform
1. Connect GitHub repository
2. Select backend folder
3. Add environment variables
4. Choose appropriate plan
5. Deploy

---

## 🌐 Admin Panel Deployment

### Build for Production

```bash
cd admin-panel
npm ci
npm run build
```

This creates an optimized build in the `dist/` folder.

### Static Hosting

#### Netlify
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
cd admin-panel
netlify deploy --prod --dir=dist
```

Create `netlify.toml`:
```toml
[build]
  command = "npm run build"
  publish = "dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

#### Vercel
```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
cd admin-panel
vercel --prod
```

Create `vercel.json`:
```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

#### AWS S3 + CloudFront
```bash
# Build
npm run build

# Upload to S3
aws s3 sync dist/ s3://khabarlagbe-admin --delete

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_DIST_ID --paths "/*"
```

#### Nginx
```nginx
server {
    listen 80;
    server_name admin.khabarlagbe.com;
    root /var/www/khabarlagbe-admin;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # Gzip compression
    gzip on;
    gzip_types text/css application/javascript application/json;
}
```

---

## 📱 Android Apps Deployment

### Google Play Store Release

#### 1. Prepare Release Build

**For User App:**
```bash
cd app
./gradlew bundleRelease
```

**For Rider App:**
```bash
cd rider-app
./gradlew bundleRelease
```

**For Restaurant App:**
```bash
cd restaurant-app
./gradlew bundleRelease
```

#### 2. Sign the Bundle

Create keystore (one-time):
```bash
keytool -genkey -v -keystore khabarlagbe.keystore -alias khabarlagbe -keyalg RSA -keysize 2048 -validity 10000
```

Configure in `build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../khabarlagbe.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "khabarlagbe"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

#### 3. Upload to Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create app (or select existing)
3. Navigate to Release > Production
4. Upload AAB file from `app/build/outputs/bundle/release/`
5. Fill in release notes
6. Submit for review

#### 4. Play Store Listing

**User App:**
- **Title**: KhabarLagbe - Food Delivery Bangladesh
- **Short Description**: Order food from your favorite restaurants in Bangladesh
- **Category**: Food & Drink
- **Content Rating**: Everyone

**Rider App:**
- **Title**: KhabarLagbe Rider - Delivery App
- **Short Description**: Earn money delivering food with KhabarLagbe
- **Category**: Business
- **Content Rating**: Everyone

**Restaurant App:**
- **Title**: KhabarLagbe Restaurant - Business
- **Short Description**: Manage your restaurant orders and menu
- **Category**: Business
- **Content Rating**: Everyone

---

## 🍎 iOS App Deployment

### App Store Release

#### 1. Prepare Archive

1. Open Xcode
2. Select "Any iOS Device" as target
3. Product > Archive
4. Wait for archive to complete

#### 2. Configure App Store Connect

1. Go to [App Store Connect](https://appstoreconnect.apple.com)
2. Create new app
3. Fill in app information:
   - **Name**: KhabarLagbe
   - **Bundle ID**: com.noor.khabarlagbe
   - **SKU**: KHABARLAGBE001
   - **Category**: Food & Drink

#### 3. Upload Build

1. In Xcode Organizer, select archive
2. Click "Distribute App"
3. Select "App Store Connect"
4. Upload
5. Wait for processing (can take 30+ minutes)

#### 4. Submit for Review

1. In App Store Connect, select build
2. Add screenshots (required sizes):
   - 6.5" iPhone: 1284 x 2778
   - 5.5" iPhone: 1242 x 2208
   - 12.9" iPad: 2048 x 2732
3. Fill in app description
4. Set pricing (Free)
5. Submit for review

---

## ⚙️ Environment Configuration

### Development
```env
NODE_ENV=development
API_URL=http://localhost:5000
DEBUG=true
```

### Staging
```env
NODE_ENV=staging
API_URL=https://staging-api.khabarlagbe.com
DEBUG=true
```

### Production
```env
NODE_ENV=production
API_URL=https://api.khabarlagbe.com
DEBUG=false
```

---

## 📊 Monitoring & Maintenance

### Application Monitoring

**Backend:**
- Use PM2 monitoring: `pm2 monit`
- Set up error tracking with Sentry
- Monitor API response times
- Track error rates

**Admin Panel:**
- Google Analytics for user behavior
- Error boundary for crash reporting
- Performance monitoring with Lighthouse

**Mobile Apps:**
- Firebase Crashlytics for crash reporting
- Firebase Performance Monitoring
- Firebase Analytics for user engagement

### Database Maintenance

```bash
# MongoDB backup
mongodump --uri="mongodb+srv://..." --out=/backup/$(date +%Y%m%d)

# MongoDB restore
mongorestore --uri="mongodb+srv://..." /backup/20240113

# Automated backups (cron)
0 2 * * * /usr/bin/mongodump --uri="$MONGODB_URI" --out=/backups/$(date +\%Y\%m\%d)
```

### Log Management

```bash
# View PM2 logs
pm2 logs khabarlagbe-api

# View nginx logs
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# Rotate logs
pm2 install pm2-logrotate
pm2 set pm2-logrotate:max_size 10M
pm2 set pm2-logrotate:retain 30
```

### SSL Certificate Renewal

```bash
# Using Let's Encrypt
certbot renew --nginx
certbot renew --dry-run  # Test renewal

# Auto-renewal (cron)
0 0 1 * * certbot renew --quiet
```

### Health Checks

Create health check endpoints:

**Backend API:**
```javascript
app.get('/health', (req, res) => {
  res.json({
    status: 'OK',
    timestamp: new Date(),
    uptime: process.uptime(),
    mongodb: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected'
  })
})
```

**Monitor with:**
- UptimeRobot
- Pingdom
- StatusCake

### Scaling

**Horizontal Scaling:**
```bash
# Add more PM2 instances
pm2 scale khabarlagbe-api +3

# Use load balancer (nginx)
upstream backend {
    server 127.0.0.1:5000;
    server 127.0.0.1:5001;
    server 127.0.0.1:5002;
}
```

**Database Scaling:**
- MongoDB sharding
- Read replicas
- Redis caching layer

---

## 🚨 Troubleshooting

### Common Issues

**1. Build Failures**
```bash
# Clear build cache
./gradlew clean
rm -rf node_modules package-lock.json
npm install
```

**2. Database Connection**
- Check MongoDB URI
- Verify network access
- Check firewall rules

**3. SSL Issues**
```bash
# Test SSL
openssl s_client -connect api.khabarlagbe.com:443

# Verify certificate
certbot certificates
```

**4. Memory Issues**
```bash
# Increase Node.js memory
NODE_OPTIONS="--max-old-space-size=4096" npm start

# Monitor memory
pm2 monit
```

---

## 📞 Support

For deployment issues:
- Create GitHub Issue
- Email: devops@khabarlagbe.com
- Documentation: https://docs.khabarlagbe.com

---

**Last Updated:** January 2024
