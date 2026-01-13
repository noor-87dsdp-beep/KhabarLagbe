# KhabarLagbe iOS App

## Overview
The iOS version of KhabarLagbe food delivery app built with SwiftUI.

## Requirements
- iOS 16.0+
- Xcode 15.0+
- Swift 5.9+

## Project Structure
```
ios/KhabarLagbe/
├── KhabarLagbeApp.swift          # App entry point
├── ContentView.swift              # Root view
├── Core/
│   ├── Network/                   # API client, services
│   ├── Storage/                   # UserDefaults, CoreData
│   └── Extensions/                # Swift extensions
├── Features/
│   ├── Auth/                      # Login, register screens
│   ├── Home/                      # Home screen with restaurants
│   │   └── HomeView.swift        # Main home screen
│   ├── Restaurant/                # Restaurant details
│   ├── Cart/                      # Shopping cart
│   ├── Checkout/                  # Order placement
│   ├── OrderTracking/             # Live order tracking
│   └── Profile/                   # User profile
├── Shared/
│   ├── Components/                # Reusable UI components
│   └── Theme/                     # Colors, fonts, styles
└── Resources/
    ├── Assets.xcassets           # Images, icons
    └── Localizable.strings       # Localization (en, bn)
```

## Features

### Implemented
- ✅ Home screen with restaurant list
- ✅ Location selector
- ✅ Search bar
- ✅ Category filters
- ✅ Promotional banners
- ✅ Restaurant cards with ratings

### To Be Implemented
- Restaurant details screen
- Menu browsing
- Cart management
- Checkout flow
- Order tracking with maps
- User profile and settings
- Payment integration (bKash, Nagad, SSL Commerz)
- Push notifications
- Real-time order updates

## Setup Instructions

1. Open the project in Xcode:
   ```bash
   cd ios
   open KhabarLagbe.xcodeproj
   ```

2. Configure the development team:
   - Select the KhabarLagbe target
   - Go to Signing & Capabilities
   - Select your development team

3. Install dependencies (if using Swift Package Manager):
   - Dependencies will be automatically resolved by Xcode

4. Run the app:
   - Select a simulator or device
   - Press ⌘R or click the Run button

## Bangladesh Localization

The app supports:
- Bangla language interface
- BDT currency (৳)
- Bangladesh-specific payment methods
- Local addresses and locations

## API Integration

Configure the API base URL in `Core/Network/APIClient.swift`:
```swift
let baseURL = "https://api.khabarlagbe.com"
```

## Dependencies

- SwiftUI for UI
- Combine for reactive programming
- MapKit for maps and location
- URLSession for networking
- UserDefaults for local storage

## Development Guidelines

1. Use SwiftUI for all UI components
2. Follow MVVM architecture pattern
3. Use Combine for state management
4. Write unit tests for business logic
5. Use Swift's modern concurrency (async/await)
6. Follow Swift naming conventions
7. Keep views small and composable

## Building for Production

1. Update version and build number
2. Configure release build settings
3. Archive the app (Product > Archive)
4. Upload to App Store Connect
5. Submit for review

## License

MIT License - See main repository LICENSE file
