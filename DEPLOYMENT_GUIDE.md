# KhabarLagbe - Complete Deployment Guide

## Overview

This guide covers deploying all components of the KhabarLagbe platform to production.

## 🏗️ Architecture Overview

```
┌─────────────────┐
│  Android Apps   │  ← User, Rider, Restaurant Apps
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│   Load Balancer │  ← Nginx/AWS ALB
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ↓         ↓
┌────────┐ ┌────────┐
│ API 1  │ │ API 2  │  ← Node.js Backend (Horizontal Scaling)
└───┬────┘ └───┬────┘
    │          │
    └────┬─────┘
         │
    ┌────┴────┐
    │         │
    ↓         ↓
┌────────┐ ┌────────┐
│MongoDB │ │ Redis  │  ← Databases
└────────┘ └────────┘
```

---

## 📋 Prerequisites

### Required Accounts
- [ ] AWS Account (or equivalent cloud provider)
- [ ] MongoDB Atlas Account
- [ ] Redis Cloud Account
- [ ] Firebase Account (for FCM)
- [ ] bKash Merchant Account
- [ ] Nagad Merchant Account
- [ ] SSL Commerz Account
- [ ] Domain Name (e.g., khabarlagbe.com)
- [ ] SSL Certificate

### Required Tools
- [ ] Node.js 18+
- [ ] npm or yarn
- [ ] Git
- [ ] Docker & Docker Compose
- [ ] kubectl (for Kubernetes)
- [ ] PM2 (for process management)

---

## 🗄️ Database Setup

### MongoDB Atlas

1. **Create Cluster**
```bash
# Go to https://cloud.mongodb.com
# Create new cluster (M10+ for production)
# Select region: Asia Pacific (Singapore/Mumbai)
```

2. **Configure Network Access**
```bash
# Add IP addresses of your servers
# Or use 0.0.0.0/0 (not recommended for production)
```

3. **Create Database User**
```bash
Username: khabarlagbe_admin
Password: <strong_password>
Role: readWrite on khabarlagbe database
```

4. **Get Connection String**
```
mongodb+srv://khabarlagbe_admin:<password>@cluster.mongodb.net/khabarlagbe?retryWrites=true&w=majority
```

### Redis Cloud

1. **Create Redis Instance**
```bash
# Go to https://redis.com/cloud
# Create new subscription
# Select 256MB+ for production
```

2. **Get Connection URL**
```
redis://default:<password>@redis-endpoint.com:port
```

---

## 🔐 Environment Variables

### Backend API

Create `.env` file:

```bash
# Production Configuration
NODE_ENV=production
PORT=5000
API_VERSION=v1

# Database
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/khabarlagbe
REDIS_URL=redis://default:pass@redis.com:port

# JWT Secrets (generate with: openssl rand -hex 64)
JWT_ACCESS_SECRET=your_production_access_secret
JWT_REFRESH_SECRET=your_production_refresh_secret
JWT_ACCESS_EXPIRE=15m
JWT_REFRESH_EXPIRE=7d

# CORS
CORS_ORIGIN=https://khabarlagbe.com,https://admin.khabarlagbe.com

# Firebase
FIREBASE_PROJECT_ID=khabarlagbe-prod
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk@khabarlagbe-prod.iam.gserviceaccount.com

# Payment Gateways - Production Credentials
BKASH_APP_KEY=your_production_bkash_app_key
BKASH_APP_SECRET=your_production_bkash_app_secret
BKASH_USERNAME=your_production_bkash_username
BKASH_PASSWORD=your_production_bkash_password
BKASH_BASE_URL=https://tokenized.pay.bka sh.com

NAGAD_MERCHANT_ID=your_merchant_id
NAGAD_MERCHANT_NUMBER=your_merchant_number
NAGAD_PUBLIC_KEY=your_public_key
NAGAD_PRIVATE_KEY=your_private_key
NAGAD_BASE_URL=https://api.mynagad.com

SSLCOMMERZ_STORE_ID=your_store_id
SSLCOMMERZ_STORE_PASSWORD=your_store_password
SSLCOMMERZ_BASE_URL=https://securepay.sslcommerz.com

# Frontend URLs
FRONTEND_URL=https://khabarlagbe.com
ADMIN_URL=https://admin.khabarlagbe.com
```

---

## 🚀 Backend Deployment

### Option 1: AWS EC2 with PM2

#### 1. Launch EC2 Instance
```bash
# Launch Ubuntu 22.04 LTS
# Instance Type: t3.medium or larger
# Security Group: Allow ports 22, 80, 443, 5000
```

#### 2. Connect and Setup
```bash
# SSH into server
ssh -i key.pem ubuntu@your-server-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Install PM2
sudo npm install -g pm2

# Install Nginx
sudo apt install -y nginx
```

#### 3. Deploy Backend
```bash
# Clone repository
git clone https://github.com/noor-87dsdp-beep/KhabarLagbe.git
cd KhabarLagbe/backend

# Install dependencies
npm ci --production

# Copy environment file
nano .env  # Paste production environment variables

# Start with PM2
pm2 start src/server.js --name khabarlagbe-api -i max

# Save PM2 configuration
pm2 save
pm2 startup
```

#### 4. Configure Nginx
```bash
sudo nano /etc/nginx/sites-available/khabarlagbe
```

```nginx
server {
    listen 80;
    server_name api.khabarlagbe.com;

    location / {
        proxy_pass http://localhost:5000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_cache_bypass $http_upgrade;
    }

    # WebSocket support
    location /socket.io {
        proxy_pass http://localhost:5000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/khabarlagbe /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 5. Setup SSL with Let's Encrypt
```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d api.khabarlagbe.com
```

### Option 2: Docker Deployment

#### 1. Create Dockerfile
Already exists in `backend/Dockerfile`

#### 2. Build and Run
```bash
# Build image
docker build -t khabarlagbe-backend:latest .

# Run container
docker run -d \
  --name khabarlagbe-api \
  --restart unless-stopped \
  -p 5000:5000 \
  --env-file .env \
  khabarlagbe-backend:latest
```

#### 3. Docker Compose
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  api:
    image: khabarlagbe-backend:latest
    restart: always
    ports:
      - "5000:5000"
    env_file:
      - .env
    depends_on:
      - redis
    networks:
      - khabarlagbe-network

  redis:
    image: redis:7-alpine
    restart: always
    volumes:
      - redis-data:/data
    networks:
      - khabarlagbe-network

  nginx:
    image: nginx:alpine
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - api
    networks:
      - khabarlagbe-network

volumes:
  redis-data:

networks:
  khabarlagbe-network:
    driver: bridge
```

```bash
docker-compose -f docker-compose.prod.yml up -d
```

---

## 📱 Admin Panel Deployment

### Build for Production

```bash
cd admin-panel

# Install dependencies
npm ci

# Create production .env
cat > .env.production << EOL
VITE_API_BASE_URL=https://api.khabarlagbe.com/api/v1
VITE_SOCKET_URL=https://api.khabarlagbe.com
VITE_MAPBOX_TOKEN=your_mapbox_token
EOL

# Build
npm run build
```

### Deploy to Netlify/Vercel

#### Netlify
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Deploy
cd dist
netlify deploy --prod
```

#### Vercel
```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
vercel --prod
```

### Deploy to AWS S3 + CloudFront

```bash
# Build
npm run build

# Upload to S3
aws s3 sync dist/ s3://admin.khabarlagbe.com --delete

# Invalidate CloudFront cache
aws cloudfront create-invalidation --distribution-id YOUR_ID --paths "/*"
```

---

## 📦 Android Apps Deployment

### 1. Update API URLs

Update `build.gradle` or config files:
```kotlin
// BuildConfig
buildTypes {
    release {
        buildConfigField("String", "API_BASE_URL", "\"https://api.khabarlagbe.com/api/v1\"")
        buildConfigField("String", "SOCKET_URL", "\"https://api.khabarlagbe.com\"")
    }
}
```

### 2. Generate Release APK

```bash
cd app  # or rider-app, restaurant-app
./gradlew assembleRelease
```

### 3. Sign APK

```bash
# Generate keystore (first time only)
keytool -genkey -v -keystore khabarlagbe.keystore -alias khabarlagbe -keyalg RSA -keysize 2048 -validity 10000

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore khabarlagbe.keystore app/build/outputs/apk/release/app-release-unsigned.apk khabarlagbe
```

### 4. Optimize with zipalign
```bash
zipalign -v 4 app-release-unsigned.apk app-release.apk
```

### 5. Upload to Google Play Store

1. Go to https://play.google.com/console
2. Create new application
3. Fill in store listing
4. Upload APK/AAB to production track
5. Submit for review

---

## 🔍 Monitoring & Logging

### Setup PM2 Monitoring

```bash
# Link to PM2 Plus (optional)
pm2 link <secret_key> <public_key>

# Monitor logs
pm2 logs khabarlagbe-api

# Monitor metrics
pm2 monit
```

### Setup Error Tracking (Sentry)

```bash
npm install @sentry/node

# In src/app.js
const Sentry = require('@sentry/node');

Sentry.init({
  dsn: 'your_sentry_dsn',
  environment: process.env.NODE_ENV,
});
```

### Setup Logging

```bash
npm install winston

# Configure in src/config/logger.js
```

---

## 🔒 Security Checklist

- [ ] All environment variables set correctly
- [ ] JWT secrets are strong and unique
- [ ] Database has strong password
- [ ] Redis requires authentication
- [ ] Firewall configured (only necessary ports open)
- [ ] SSL certificates installed
- [ ] Rate limiting enabled
- [ ] CORS configured for specific domains only
- [ ] File upload size limits set
- [ ] Payment gateway credentials are production keys
- [ ] API keys rotated regularly
- [ ] Backup strategy in place

---

## 📊 Performance Optimization

### Enable Caching

```nginx
# Nginx caching
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m max_size=1g inactive=60m;

location /api/v1/restaurants {
    proxy_cache api_cache;
    proxy_cache_valid 200 5m;
    proxy_cache_key "$scheme$request_method$host$request_uri";
    add_header X-Cache-Status $upstream_cache_status;
}
```

### Database Indexing

```javascript
// Ensure all indexes are created
db.restaurants.createIndex({ location: "2dsphere" });
db.restaurants.createIndex({ name: "text" });
db.orders.createIndex({ userId: 1, createdAt: -1 });
db.riders.createIndex({ currentLocation: "2dsphere" });
```

### Enable Compression

```javascript
// In src/app.js
const compression = require('compression');
app.use(compression());
```

---

## 🔄 CI/CD Pipeline

### GitHub Actions

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to Server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ubuntu
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /home/ubuntu/KhabarLagbe/backend
            git pull origin main
            npm ci --production
            pm2 reload khabarlagbe-api

  deploy-admin:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and Deploy
        run: |
          cd admin-panel
          npm ci
          npm run build
          netlify deploy --prod --dir=dist
        env:
          NETLIFY_AUTH_TOKEN: ${{ secrets.NETLIFY_TOKEN }}
```

---

## 💾 Backup Strategy

### Database Backup

```bash
# Automated daily backup
crontab -e

# Add cron job
0 2 * * * mongodump --uri="$MONGODB_URI" --out=/backups/$(date +\%Y\%m\%d)
```

### Application Files Backup

```bash
# Backup uploaded files
0 3 * * * tar -czf /backups/uploads-$(date +\%Y\%m\%d).tar.gz /home/ubuntu/KhabarLagbe/backend/uploads
```

---

## 🚨 Troubleshooting

### Check API Health
```bash
curl https://api.khabarlagbe.com/health
```

### Check Logs
```bash
pm2 logs khabarlagbe-api --lines 100
```

### Restart Services
```bash
pm2 restart khabarlagbe-api
sudo systemctl restart nginx
```

### Database Connection Issues
```bash
# Test MongoDB connection
mongo "mongodb+srv://user:pass@cluster.mongodb.net/test"
```

---

## 📞 Support

For deployment issues:
- Email: devops@khabarlagbe.com
- Slack: #devops-support

---

**Deployment Checklist Complete! 🎉**
