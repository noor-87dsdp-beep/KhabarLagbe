# KhabarLagbe - System Architecture

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         KhabarLagbe Platform                      │
└─────────────────────────────────────────────────────────────────┘

                        ┌──────────────┐
                        │   Mapbox     │
                        │   Services   │
                        └──────────────┘
                               │
                               │ API
                               ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   User App   │◄────►│   Backend    │◄────►│  Rider App   │
│  (Android)   │      │   API Server │      │  (Android)   │
└──────────────┘      │              │      └──────────────┘
                      │  Node.js/    │
┌──────────────┐      │  Django      │      ┌──────────────┐
│ Restaurant   │◄────►│              │◄────►│    Admin     │
│    App       │      │  Socket.IO   │      │    Panel     │
│  (Android)   │      │  WebSockets  │      │   (Web)      │
└──────────────┘      └──────────────┘      └──────────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
                    ▼                     ▼
            ┌──────────────┐      ┌──────────────┐
            │  PostgreSQL  │      │   Firebase   │
            │   Database   │      │   Services   │
            └──────────────┘      └──────────────┘
                    │                     │
                    │              ┌──────┴───────┐
                    │              │              │
                    ▼              ▼              ▼
            ┌──────────────┐  ┌────────┐  ┌──────────┐
            │    Redis     │  │  Auth  │  │   FCM    │
            │    Cache     │  └────────┘  │  Push    │
            └──────────────┘              └──────────┘
```

## Application Architecture

### User App (Android - Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                         User App                              │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────── Presentation Layer ─────────────────┐│
│  │                                                           ││
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐        ││
│  │  │   Home     │  │ Restaurant │  │   Cart     │        ││
│  │  │   Screen   │  │   Details  │  │   Screen   │        ││
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘        ││
│  │        │                │                │               ││
│  │  ┌─────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐        ││
│  │  │ ViewModel  │  │ ViewModel  │  │ ViewModel  │        ││
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘        ││
│  └────────┼────────────────┼────────────────┼──────────────┘│
│           │                │                │                │
│  ┌────────┼────────────────┼────────────────┼──────────────┐│
│  │        │    Domain Layer │                │              ││
│  │  ┌─────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐        ││
│  │  │  Get       │  │  Place     │  │  Track     │        ││
│  │  │Restaurants │  │   Order    │  │   Order    │        ││
│  │  │  UseCase   │  │  UseCase   │  │  UseCase   │        ││
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘        ││
│  │        │                │                │               ││
│  │  ┌─────▼────────────────▼────────────────▼──────┐       ││
│  │  │         Repository Interfaces                 │       ││
│  │  └──────────────────────┬────────────────────────┘       ││
│  └─────────────────────────┼────────────────────────────────┘│
│                            │                                  │
│  ┌─────────────────────────▼────────────────────────────────┐│
│  │                    Data Layer                             ││
│  │                                                            ││
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   ││
│  │  │   Remote     │  │    Local     │  │   Mapbox     │   ││
│  │  │  Data Source │  │ Data Source  │  │    Client    │   ││
│  │  │  (Retrofit)  │  │   (Room)     │  │    (Maps)    │   ││
│  │  └──────────────┘  └──────────────┘  └──────────────┘   ││
│  └───────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

## Data Flow

### Order Placement Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│   User   │     │   User   │     │ Backend  │     │Restaurant│
│  Opens   │────►│  Places  │────►│   API    │────►│  Receives│
│  Menu    │     │  Order   │     │          │     │  Order   │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
                                         │
                                         │ Notify
                                         ▼
                                   ┌──────────┐
                                   │  Rider   │
                                   │ Notified │
                                   └──────────┘
```

### Real-time Tracking Flow

```
┌──────────┐                 ┌──────────┐                 ┌──────────┐
│  Rider   │   Location      │ Backend  │   WebSocket    │   User   │
│  Device  │────────────────►│ Server   │────────────────►│   App    │
│          │   Updates       │          │   Push         │          │
└──────────┘   (10s)         └──────────┘   Updates      └──────────┘
                                    │
                                    │ Store
                                    ▼
                              ┌──────────┐
                              │  Redis   │
                              │  Cache   │
                              └──────────┘
```

## Database Schema Overview

### Core Tables

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│    users    │      │restaurants  │      │   orders    │
├─────────────┤      ├─────────────┤      ├─────────────┤
│ id          │      │ id          │      │ id          │
│ name        │      │ name        │      │ user_id     │──┐
│ email       │      │ description │      │ restaurant  │  │
│ phone       │      │ latitude    │      │ rider_id    │  │
│ password    │      │ longitude   │      │ status      │  │
│ role        │      │ rating      │      │ total       │  │
└─────────────┘      │ is_active   │      └─────────────┘  │
                     └─────────────┘                        │
                                                            │
┌─────────────┐      ┌─────────────┐                       │
│  addresses  │◄─────┤order_items  │◄──────────────────────┘
├─────────────┤      ├─────────────┤
│ id          │      │ id          │
│ user_id     │      │ order_id    │
│ address     │      │ menu_item   │
│ latitude    │      │ quantity    │
│ longitude   │      │ price       │
└─────────────┘      └─────────────┘

┌─────────────┐      ┌─────────────┐
│ menu_items  │      │   riders    │
├─────────────┤      ├─────────────┤
│ id          │      │ id          │
│ restaurant  │      │ name        │
│ name        │      │ phone       │
│ price       │      │ vehicle     │
│ image_url   │      │ rating      │
│ is_available│      │ is_active   │
└─────────────┘      └─────────────┘
```

## Technology Stack Summary

### Frontend (User App)
- **UI**: Jetpack Compose
- **Language**: Kotlin
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + OkHttp
- **Images**: Coil
- **Maps**: Mapbox
- **Database**: Room
- **Async**: Coroutines + Flow
- **Storage**: DataStore

### Backend
- **Runtime**: Node.js / Python
- **Framework**: Express / Django / FastAPI
- **Database**: PostgreSQL / MongoDB
- **Cache**: Redis
- **Real-time**: Socket.IO
- **Auth**: JWT
- **Storage**: AWS S3 / Firebase Storage
- **Payment**: Stripe / Razorpay
- **Push**: Firebase Cloud Messaging
- **Email**: SendGrid
- **SMS**: Twilio

### Infrastructure
- **Hosting**: AWS / DigitalOcean / Heroku
- **CI/CD**: GitHub Actions
- **Monitoring**: Sentry / DataDog
- **Logging**: CloudWatch / Loggly
- **CDN**: CloudFront / Cloudflare

## Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Security Layers                       │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  1. Authentication                                        │
│     ├─ JWT Tokens (Access + Refresh)                     │
│     ├─ Email/Phone Verification                          │
│     └─ Social OAuth (Google, Facebook)                   │
│                                                           │
│  2. Authorization                                         │
│     ├─ Role-Based Access Control (RBAC)                  │
│     ├─ API Key Management                                │
│     └─ Permission Checks                                 │
│                                                           │
│  3. Data Protection                                       │
│     ├─ HTTPS/TLS Encryption                              │
│     ├─ Password Hashing (bcrypt)                         │
│     ├─ Database Encryption                               │
│     └─ Secure Storage (Keychain/KeyStore)                │
│                                                           │
│  4. API Security                                          │
│     ├─ Rate Limiting                                      │
│     ├─ Input Validation                                   │
│     ├─ SQL Injection Prevention                           │
│     └─ XSS Protection                                     │
│                                                           │
│  5. Payment Security                                      │
│     ├─ PCI DSS Compliance                                │
│     ├─ Tokenization                                       │
│     └─ 3D Secure                                          │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

## Scalability Strategy

### Horizontal Scaling
- Load balancer (Nginx/AWS ALB)
- Multiple API servers
- Database replication (master-slave)
- Redis cluster for caching
- CDN for static assets

### Vertical Scaling
- Optimized database queries
- Efficient data models
- Connection pooling
- Query result caching
- Background job processing

### Performance Optimization
- Image compression
- Lazy loading
- API response caching
- Database indexing
- Code splitting
- Tree shaking

## Deployment Architecture

```
┌────────────────────────────────────────────────────────┐
│                    Production                           │
├────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────┐          ┌──────────┐                    │
│  │   CDN    │          │  Load    │                    │
│  │CloudFront│          │ Balancer │                    │
│  └────┬─────┘          └────┬─────┘                    │
│       │                     │                           │
│       │              ┌──────┴──────┐                    │
│       │              │             │                    │
│       │         ┌────▼───┐    ┌───▼────┐               │
│       │         │ API    │    │  API   │               │
│       │         │Server 1│    │Server 2│               │
│       │         └────┬───┘    └───┬────┘               │
│       │              │            │                     │
│       │         ┌────▼────────────▼────┐               │
│       │         │   PostgreSQL        │               │
│       │         │   (Master-Slave)     │               │
│       │         └─────────────────────┘               │
│       │                                                │
│  ┌────▼─────┐       ┌──────────┐                      │
│  │  Static  │       │  Redis   │                      │
│  │  Assets  │       │  Cluster │                      │
│  └──────────┘       └──────────┘                      │
│                                                         │
└────────────────────────────────────────────────────────┘
```

## Monitoring & Logging

```
┌────────────────────────────────────────────────────────┐
│              Monitoring Dashboard                       │
├────────────────────────────────────────────────────────┤
│                                                         │
│  Application Metrics                                    │
│  ├─ Request Rate                                        │
│  ├─ Response Time                                       │
│  ├─ Error Rate                                          │
│  └─ Active Users                                        │
│                                                         │
│  Infrastructure Metrics                                 │
│  ├─ CPU Usage                                           │
│  ├─ Memory Usage                                        │
│  ├─ Disk I/O                                            │
│  └─ Network Traffic                                     │
│                                                         │
│  Business Metrics                                       │
│  ├─ Orders per Hour                                     │
│  ├─ Revenue                                             │
│  ├─ Conversion Rate                                     │
│  └─ User Retention                                      │
│                                                         │
│  Error Tracking                                         │
│  ├─ Sentry Integration                                  │
│  ├─ Crash Reports                                       │
│  ├─ Exception Logs                                      │
│  └─ Stack Traces                                        │
│                                                         │
└────────────────────────────────────────────────────────┘
```

---

**Last Updated**: January 2026
