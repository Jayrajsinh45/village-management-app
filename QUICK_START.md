# 🚀 Quick Start Guide - Village Management App

## Prerequisites Check
Before you begin, ensure you have:
- ✅ Android Studio Hedgehog (2023.1.1) or later
- ✅ JDK 17 installed
- ✅ Android SDK 34 installed
- ✅ A Firebase account (free tier is fine)
- ✅ A Google Cloud account (for Maps API)

## Step-by-Step Setup (15-20 minutes)

### 1. Firebase Setup (5 minutes)

1. **Create Firebase Project**
   - Visit: https://console.firebase.google.com/
   - Click "Add project"
   - Name it: "Village Management" (or your choice)
   - Disable Google Analytics (optional for testing)
   - Click "Create project"

2. **Add Android App**
   - Click the Android icon
   - Package name: `com.villagemanagement`
   - App nickname: "Village Management"
   - Click "Register app"

3. **Download Configuration**
   - Download `google-services.json`
   - Replace the file at: `d:\Project\app\google-services.json`

4. **Enable Services**
   - Go to "Build" → "Authentication"
   - Click "Get started"
   - Enable "Email/Password" provider
   - Click "Save"
   
   - Go to "Build" → "Firestore Database"
   - Click "Create database"
   - Start in "Test mode" (for development)
   - Choose a location close to you
   - Click "Enable"
   
   - Go to "Build" → "Storage"
   - Click "Get started"
   - Start in "Test mode"
   - Click "Done"

### 2. Google Maps Setup (5 minutes)

1. **Enable Maps API**
   - Visit: https://console.cloud.google.com/
   - Select your Firebase project (or create new one)
   - Go to "APIs & Services" → "Library"
   - Search for "Maps SDK for Android"
   - Click it and press "Enable"

2. **Create API Key**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "API Key"
   - Copy the API key

3. **Add to Project**
   - Open `d:\Project\local.properties`
   - Replace the line with:
   ```
   MAPS_API_KEY=YOUR_ACTUAL_API_KEY_HERE
   ```

### 3. Open in Android Studio (2 minutes)

1. **Open Project**
   - Launch Android Studio
   - Click "Open"
   - Navigate to `d:\Project`
   - Click "OK"

2. **Wait for Sync**
   - Android Studio will automatically sync Gradle
   - This may take 2-5 minutes on first run
   - Wait for "Gradle sync finished" message

3. **Verify Setup**
   - Check for any errors in the "Build" tab
   - If you see errors, check that:
     - `google-services.json` is in the correct location
     - `local.properties` has the Maps API key
     - Internet connection is stable

### 4. Run the App (3 minutes)

1. **Connect Device or Start Emulator**
   
   **Option A: Physical Device**
   - Enable Developer Options on your Android phone
   - Enable USB Debugging
   - Connect via USB
   - Select your device from the device dropdown
   
   **Option B: Emulator**
   - Click "Device Manager" in Android Studio
   - Click "Create Device"
   - Select "Pixel 6" or any device
   - Select "API 34" (Android 14)
   - Click "Finish"
   - Start the emulator

2. **Build and Run**
   - Click the green "Run" button (or press Shift+F10)
   - Wait for the app to build and install
   - The app should launch automatically

### 5. Test the App (5 minutes)

1. **Register First User (Super Admin)**
   - The app will show the splash screen
   - Then navigate to the login screen
   - Click "Register"
   - Fill in the form:
     - Name: Your Name
     - Email: admin@test.com
     - Phone: 1234567890
     - Password: Test123!
     - Confirm Password: Test123!
   - Click "Register"

2. **Explore the App**
   - You'll be logged in automatically
   - Explore the home screen
   - Try the "Villages" button (will be empty initially)
   - Check your profile

3. **Create a Test Village (Manual - via Firebase Console)**
   - Go to Firebase Console → Firestore Database
   - Click "Start collection"
   - Collection ID: `villages`
   - Add document with these fields:
     ```
     name: "Test Village"
     description: "A test village for development"
     population: 1000
     adminId: [your user ID from users collection]
     adminName: "Your Name"
     adminContact: "1234567890"
     area: "5"
     imageUrl: ""
     isActive: true
     location: (map)
       - latitude: 28.6139
       - longitude: 77.2090
       - address: "New Delhi, India"
     ```
   - Click "Save"

4. **Verify in App**
   - Go back to the app
   - Pull down to refresh or restart
   - You should see the test village

## 🎉 Success!

You now have a working Village Management app! 

## Next Steps

1. **Read the Documentation**
   - Check `README.md` for detailed information
   - Review `SETUP_CHECKLIST.md` for development roadmap

2. **Explore the Code**
   - Start with `MainActivity.kt`
   - Check out the screens in `ui/screens/`
   - Review data models in `data/model/`

3. **Start Developing**
   - Pick a feature from Phase 2 in SETUP_CHECKLIST.md
   - Implement and test
   - Commit your changes

## 🐛 Troubleshooting

### "google-services.json not found"
- Make sure you downloaded the file from Firebase
- Place it at: `d:\Project\app\google-services.json`
- Sync Gradle again

### "MAPS_API_KEY not found"
- Check `local.properties` file exists
- Verify the API key is correct
- Make sure there are no extra spaces

### "Build failed" errors
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches → Invalidate and Restart

### App crashes on launch
- Check Logcat for error messages
- Verify Firebase services are enabled
- Make sure you're using a device/emulator with API 24+

### Can't register users
- Check Firebase Authentication is enabled
- Verify Email/Password provider is active
- Check internet connection

## 📞 Need Help?

- Check the error message in Logcat
- Review Firebase Console for service status
- Verify all setup steps were completed
- Check that dependencies are up to date

---

**Happy Coding! 🎉**
