# Village Management Android Application

A comprehensive multi-village management platform built with modern Android development practices.

## 🏘️ Project Overview

This Android application enables dynamic registration and management of multiple villages. Each village has its own admin who manages village-specific data and residents. The platform facilitates community engagement through help requests, suggestions, and interactive maps.

## ✨ Features

### Phase 1 - MVP (Current Implementation)
- ✅ **Authentication & User Management**
  - Email/Phone registration
  - Login/Logout functionality
  - Role-based access (Super Admin, Village Admin, Resident)
  - User profile management

- ✅ **Village Management**
  - Add new villages (Super Admin)
  - Village profile pages
  - Population tracking
  - Admin contact information
  - Search functionality

- 🚧 **Interactive Map** (Placeholder)
  - Google Maps integration
  - Village location display
  - Important locations marking

- 🚧 **Help Section** (Placeholder)
  - Help request submission
  - Category management
  - Status tracking

- 🚧 **Suggestion Section** (Placeholder)
  - Suggestion submission
  - Upvoting system
  - Admin review

- 🚧 **Residents Directory** (Placeholder)
  - Resident management (Admin only)
  - Demographics tracking

## 🛠️ Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose with Material Design 3
- **Backend**: Firebase
  - Authentication
  - Firestore Database
  - Cloud Storage
  - Cloud Messaging
- **Maps**: Google Maps SDK
- **Dependency Injection**: Hilt
- **Async Operations**: Kotlin Coroutines & Flow
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Compose
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/villagemanagement/
│   │   ├── data/
│   │   │   ├── model/          # Data models
│   │   │   └── repository/     # Repository layer
│   │   ├── di/                 # Dependency injection modules
│   │   ├── ui/
│   │   │   ├── navigation/     # Navigation setup
│   │   │   ├── screens/        # Composable screens
│   │   │   ├── theme/          # Material 3 theme
│   │   │   ├── viewmodel/      # ViewModels
│   │   │   └── MainActivity.kt
│   │   ├── utils/              # Utility classes
│   │   └── VillageManagementApp.kt
│   ├── res/                    # Resources
│   └── AndroidManifest.xml
├── build.gradle.kts
└── google-services.json        # Firebase config (needs setup)
```

## 🚀 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Firebase account
- Google Cloud account (for Maps API)

### Step 1: Clone the Repository
```bash
cd d:/Project
```

### Step 2: Firebase Setup

1. **Create a Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add an Android app with package name: `com.villagemanagement`

2. **Download google-services.json**
   - Download the `google-services.json` file from Firebase Console
   - Replace the placeholder file at `app/google-services.json`

3. **Enable Firebase Services**
   - **Authentication**: Enable Email/Password authentication
   - **Firestore**: Create a database in production mode
   - **Storage**: Enable Cloud Storage
   - **Cloud Messaging**: Enable FCM for notifications

4. **Firestore Security Rules** (Initial setup - adjust for production)
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /villages/{villageId} {
         allow read: if request.auth != null;
         allow write: if request.auth != null && 
           (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'SUPER_ADMIN');
       }
       match /help_requests/{requestId} {
         allow read: if request.auth != null;
         allow create: if request.auth != null;
         allow update, delete: if request.auth != null && 
           (get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role in ['VILLAGE_ADMIN', 'SUPER_ADMIN']);
       }
       match /suggestions/{suggestionId} {
         allow read: if request.auth != null;
         allow create: if request.auth != null;
         allow update: if request.auth != null;
       }
     }
   }
   ```

### Step 3: Google Maps Setup

1. **Get Google Maps API Key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Enable Maps SDK for Android
   - Create an API key

2. **Add API Key to Project**
   - Open `local.properties`
   - Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key
   ```properties
   MAPS_API_KEY=AIzaSy...your_actual_key
   ```

### Step 4: Build and Run

1. **Open in Android Studio**
   ```bash
   # Open Android Studio and select "Open an Existing Project"
   # Navigate to d:/Project
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

3. **Run the App**
   - Connect an Android device or start an emulator
   - Click Run (Shift+F10)

## 👥 User Roles

### Super Admin
- Create and manage villages
- Assign village admins
- Full system access

### Village Admin
- Manage specific village data
- View and update help requests
- Review suggestions
- Access residents directory

### Resident
- View village information
- Submit help requests
- Submit and upvote suggestions
- View village map

## 🔐 Default Test Accounts

After setting up Firebase, you'll need to create test accounts manually through the app's registration flow or Firebase Console.

**Recommended Test Setup:**
1. Create a Super Admin account first
2. Use Super Admin to create villages
3. Create Village Admin accounts and assign to villages
4. Create Resident accounts for testing

## 📱 Screens

1. **Splash Screen** - App initialization
2. **Login Screen** - User authentication
3. **Register Screen** - New user registration
4. **Home Screen** - Dashboard with quick actions
5. **Village List** - All villages with search
6. **Village Details** - Detailed village information
7. **Map Screen** - Interactive village map (Coming Soon)
8. **Help Section** - Help requests management (Coming Soon)
9. **Suggestions** - Community suggestions (Coming Soon)
10. **Profile** - User profile management (Coming Soon)
11. **Admin Panel** - Administrative functions (Coming Soon)

## 🔄 Development Phases

### ✅ Phase 1: Foundation (Completed)
- Project setup
- Authentication system
- Basic village management
- Navigation structure
- Material 3 UI

### 🚧 Phase 2: Core Features (Next)
- Google Maps integration
- Help request system
- Image upload functionality
- Enhanced village management

### 📋 Phase 3: Community Features
- Suggestion system with upvoting
- Residents directory
- Demographics dashboard
- Advanced search and filters

### 🎯 Phase 4: Polish & Deploy
- Push notifications
- Offline support
- Performance optimization
- Testing
- Play Store deployment

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## 📝 Firebase Collections Structure

### users
```javascript
{
  id: string,
  email: string,
  phone: string,
  name: string,
  profilePhotoUrl: string,
  role: "SUPER_ADMIN" | "VILLAGE_ADMIN" | "RESIDENT",
  villageId: string,
  isActive: boolean,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### villages
```javascript
{
  id: string,
  name: string,
  description: string,
  location: {
    latitude: number,
    longitude: number,
    address: string
  },
  adminId: string,
  adminName: string,
  adminContact: string,
  population: number,
  area: string,
  imageUrl: string,
  isActive: boolean,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

### help_requests
```javascript
{
  id: string,
  villageId: string,
  userId: string,
  userName: string,
  category: "EMERGENCY" | "INFRASTRUCTURE" | "SOCIAL" | "HEALTH" | "EDUCATION" | "OTHER",
  title: string,
  description: string,
  status: "PENDING" | "IN_PROGRESS" | "RESOLVED" | "REJECTED",
  priority: number,
  imageUrls: array,
  adminNotes: string,
  createdAt: timestamp,
  updatedAt: timestamp,
  resolvedAt: timestamp
}
```

### suggestions
```javascript
{
  id: string,
  villageId: string,
  userId: string,
  userName: string,
  title: string,
  description: string,
  category: string,
  upvotes: number,
  upvotedBy: array,
  isReviewed: boolean,
  isImplemented: boolean,
  adminResponse: string,
  createdAt: timestamp,
  updatedAt: timestamp
}
```

## 🤝 Contributing

This is a learning/development project. Contributions, issues, and feature requests are welcome!

## 📄 License

This project is for educational purposes.

## 📞 Support

For issues or questions, please create an issue in the repository.

## 🎓 Learning Resources

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase for Android](https://firebase.google.com/docs/android/setup)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Built with ❤️ using Modern Android Development practices**
