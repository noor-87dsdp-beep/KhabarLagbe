# KhabarLagbe - Setup Guide

## Quick Start

This guide will help you set up the complete KhabarLagbe food delivery platform.

## Prerequisites

### For Android Development (User App)
- **Android Studio**: Koala (2024.1.1) or newer
- **JDK**: Version 17 or newer
- **Android SDK**: API Level 35
- **Gradle**: 8.13 or newer
- **Device/Emulator**: Android 7.0 (API 24) or higher

### For Backend Development
- **Node.js**: v18 or newer (if using Node.js)
- **Python**: 3.10+ (if using Python/Django)
- **PostgreSQL**: 14+ or MongoDB 6+
- **Redis**: 7+
- **Git**: Latest version

### Third-Party Services
1. **Mapbox Account** - [Sign up](https://www.mapbox.com/)
2. **Firebase Project** - [Firebase Console](https://console.firebase.google.com/)
3. **Payment Gateway** - Stripe or Razorpay account
4. **AWS Account** (optional) - For S3 storage

## Step-by-Step Setup

### 1. Clone the Repository

```bash
git clone https://github.com/noor-87dsdp-beep/KhabarLagbe.git
cd KhabarLagbe
```

### 2. Android App Setup

#### A. Open in Android Studio
```bash
# Open Android Studio
# File -> Open -> Select KhabarLagbe folder
```

#### B. Configure Mapbox

1. Sign up at [Mapbox](https://www.mapbox.com/)
2. Get your access token from the [Account page](https://account.mapbox.com/)
3. Create `local.properties` in the project root:

```properties
# Mapbox
MAPBOX_ACCESS_TOKEN=pk.eyJ1IjoieW91ci11c2VybmFtZSIsImEiOiJjbGV4YW1wbGUxMjMifQ.example_token_here
```

4. Add to `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        // ... other config
        
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${project.findProperty("MAPBOX_ACCESS_TOKEN")}\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

#### C. Configure Firebase

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add an Android app:
   - Package name: `com.noor.khabarlagbe`
   - App nickname: KhabarLagbe User App
   - Debug signing certificate (optional)
4. Download `google-services.json`
5. Place it in `app/` directory

6. Enable Firebase services:
   - **Authentication**: Email, Phone, Google Sign-in
   - **Cloud Firestore**: Database
   - **Cloud Messaging**: Push notifications
   - **Cloud Storage**: Image uploads
   - **Analytics**: User tracking

7. Add Firebase config to your project (if not already in build.gradle):

```kotlin
// project-level build.gradle.kts
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// app-level build.gradle.kts
plugins {
    id("com.google.gms.google-services")
}
```

#### D. Build the Project

```bash
# Make gradlew executable
chmod +x gradlew

# Clean and build
./gradlew clean build

# Or use Android Studio
# Build -> Make Project
```

#### E. Run on Device/Emulator

```bash
# Via command line
./gradlew installDebug

# Or in Android Studio
# Run -> Run 'app'
```

### 3. Backend Setup (Node.js Example)

#### A. Install Dependencies

```bash
cd backend

# Initialize Node.js project (if not exists)
npm init -y

# Install dependencies
npm install express prisma @prisma/client socket.io jsonwebtoken bcryptjs cors dotenv
npm install express-validator helmet morgan compression
npm install @mapbox/mapbox-sdk stripe firebase-admin aws-sdk
npm install redis bull socket.io-redis

# Dev dependencies
npm install -D nodemon typescript @types/node @types/express
```

#### B. Configure Environment

Create `.env` file in backend directory:

```env
# Server
NODE_ENV=development
PORT=3000
API_VERSION=v1
BASE_URL=http://localhost:3000

# Database
DATABASE_URL=postgresql://username:password@localhost:5432/khabarlagbe

# Redis
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-in-production
JWT_EXPIRES_IN=15m
REFRESH_TOKEN_SECRET=your-super-secret-refresh-key
REFRESH_TOKEN_EXPIRES_IN=7d

# Mapbox
MAPBOX_ACCESS_TOKEN=pk.your_mapbox_token_here

# Firebase Admin SDK
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYour key here\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com

# Payment (Stripe)
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# AWS S3 (Optional)
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_BUCKET_NAME=khabarlagbe-uploads
AWS_REGION=us-east-1

# Email (SendGrid)
SENDGRID_API_KEY=SG.your_sendgrid_api_key
FROM_EMAIL=noreply@khabarlagbe.com

# SMS (Twilio)
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890
```

#### C. Setup Database

```bash
# Initialize Prisma
npx prisma init

# Edit prisma/schema.prisma with your models
# Then generate Prisma client
npx prisma generate

# Run migrations
npx prisma migrate dev --name init

# Seed database (optional)
npx prisma db seed
```

#### D. Start Backend Server

```bash
# Development mode
npm run dev

# Production mode
npm start
```

### 4. Database Setup

#### PostgreSQL

```bash
# Install PostgreSQL
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS
brew install postgresql

# Start PostgreSQL
sudo service postgresql start

# Create database
sudo -u postgres psql
CREATE DATABASE khabarlagbe;
CREATE USER khabarlagbe_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE khabarlagbe TO khabarlagbe_user;
\q
```

#### Redis

```bash
# Install Redis
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Start Redis
redis-server

# Test connection
redis-cli ping
# Should return PONG
```

### 5. Third-Party Service Setup

#### Mapbox Configuration

1. Create account at [Mapbox](https://www.mapbox.com/)
2. Go to [Account](https://account.mapbox.com/)
3. Create a new token with these scopes:
   - `styles:read`
   - `fonts:read`
   - `datasets:read`
   - `navigation:read`
4. Copy the token to your `.env` files

#### Firebase Detailed Setup

1. **Authentication**
   ```
   - Enable Email/Password
   - Enable Phone authentication (requires enabling App Check)
   - Enable Google Sign-In
   ```

2. **Firestore Database**
   ```
   - Create database in production mode
   - Set up security rules:
   ```
   
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /restaurants/{restaurantId} {
         allow read: if true;
         allow write: if request.auth != null;
       }
       match /orders/{orderId} {
         allow read: if request.auth != null;
         allow write: if request.auth != null;
       }
     }
   }
   ```

3. **Cloud Storage**
   ```
   - Create storage bucket
   - Set up storage rules
   ```

4. **Cloud Messaging**
   ```
   - Enable FCM
   - Download service account key for backend
   ```

5. **Get Service Account Key** for backend:
   - Project Settings → Service Accounts
   - Generate New Private Key
   - Save JSON file securely
   - Extract values for .env file

#### Stripe Setup

1. Create account at [Stripe](https://stripe.com/)
2. Get API keys from [Dashboard](https://dashboard.stripe.com/apikeys)
3. Configure webhook:
   - Dashboard → Developers → Webhooks
   - Add endpoint: `https://your-domain.com/api/v1/webhooks/stripe`
   - Select events: `payment_intent.succeeded`, `payment_intent.failed`
4. Copy webhook signing secret

### 6. Testing

#### Android App Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

#### Backend Testing

```bash
# Install test dependencies
npm install -D jest supertest @types/jest

# Run tests
npm test

# With coverage
npm run test:coverage
```

### 7. Common Issues & Solutions

#### Android Build Issues

**Problem**: "SDK location not found"
```bash
# Solution: Create local.properties
echo "sdk.dir=/path/to/Android/sdk" > local.properties
```

**Problem**: Gradle sync fails
```bash
# Solution: Clear cache
./gradlew clean
rm -rf .gradle
# Restart Android Studio
```

**Problem**: Mapbox not working
```bash
# Solution: Check token is in local.properties
# Rebuild project
./gradlew clean build
```

#### Backend Issues

**Problem**: Database connection fails
```bash
# Solution: Check DATABASE_URL in .env
# Verify PostgreSQL is running
sudo service postgresql status
```

**Problem**: Redis connection fails
```bash
# Solution: Start Redis server
redis-server
```

**Problem**: Module not found
```bash
# Solution: Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

### 8. Development Workflow

#### Android App Development

1. Create feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make changes

3. Test changes
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

4. Commit and push
   ```bash
   git add .
   git commit -m "feat: your feature description"
   git push origin feature/your-feature-name
   ```

5. Create pull request

#### Backend Development

1. Create feature branch
2. Implement changes
3. Write tests
4. Run tests
5. Commit and push
6. Create pull request

### 9. Deployment

#### Android App Deployment

1. **Generate signing key**
   ```bash
   keytool -genkey -v -keystore khabarlagbe-release-key.jks \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias khabarlagbe-release
   ```

2. **Configure signing in app/build.gradle.kts**
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("khabarlagbe-release-key.jks")
               storePassword = "your-store-password"
               keyAlias = "khabarlagbe-release"
               keyPassword = "your-key-password"
           }
       }
       
       buildTypes {
           getByName("release") {
               signingConfig = signingConfigs.getByName("release")
           }
       }
   }
   ```

3. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```

4. **Upload to Google Play Console**

#### Backend Deployment (Example: AWS EC2)

1. Launch EC2 instance
2. Install dependencies
3. Clone repository
4. Configure environment
5. Set up PM2 for process management
   ```bash
   npm install -g pm2
   pm2 start npm --name "khabarlagbe-api" -- start
   pm2 save
   pm2 startup
   ```
6. Configure Nginx reverse proxy
7. Set up SSL with Let's Encrypt
8. Configure firewall

### 10. Monitoring & Logging

- **Android**: Firebase Crashlytics
- **Backend**: Sentry, DataDog, or New Relic
- **Logs**: CloudWatch, Loggly
- **Uptime**: UptimeRobot, Pingdom

### 11. Support

- **Issues**: [GitHub Issues](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues)
- **Discussions**: [GitHub Discussions](https://github.com/noor-87dsdp-beep/KhabarLagbe/discussions)
- **Email**: support@khabarlagbe.com

### 12. Useful Commands

```bash
# Android
./gradlew clean                    # Clean build
./gradlew build                    # Build project
./gradlew assembleDebug           # Build debug APK
./gradlew assembleRelease         # Build release APK
./gradlew test                    # Run unit tests
./gradlew connectedAndroidTest    # Run instrumented tests
./gradlew lint                    # Run lint checks

# Backend
npm install                       # Install dependencies
npm run dev                       # Start dev server
npm start                         # Start prod server
npm test                          # Run tests
npm run lint                      # Run linter
npx prisma studio                 # Open Prisma Studio
npx prisma migrate dev            # Run migrations

# Database
psql -U username -d khabarlagbe   # Connect to PostgreSQL
redis-cli                         # Connect to Redis

# Git
git status                        # Check status
git log --oneline                 # View commit history
git branch                        # List branches
git checkout -b feature/name      # Create and switch to branch
```

## Next Steps

1. ✅ Complete Android User App UI
2. ⏭️ Implement authentication flow
3. ⏭️ Build backend API
4. ⏭️ Integrate Mapbox for live tracking
5. ⏭️ Add payment gateway
6. ⏭️ Develop Rider App
7. ⏭️ Develop Restaurant App
8. ⏭️ Develop Admin Panel
9. ⏭️ End-to-end testing
10. ⏭️ Deploy to production

---

**Happy Coding! 🚀**
