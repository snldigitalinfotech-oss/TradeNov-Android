# TradeNov Android Build & Deployment Guide

This guide describes how to configure environments, compile debug APKs, and produce release-ready AAB bundles for the Google Play Store.

---

## 1. Environment Requirements
- **macOS / Linux / Windows**
- **JDK 17** (Azul Zulu 17 or OpenJDK 17)
- **Android Studio** (Hedgehog 2023.1.1 or Ladybug 2024.2+)
- **Android SDK 34** (`platforms/android-34`)

Set environment variables:
```bash
export JAVA_HOME="/Users/macbook/.jdks/zulu17.52.17-ca-jdk17.0.12-macosx_x64/zulu-17.jdk/Contents/Home"
export ANDROID_HOME="/Users/macbook/Library/Android/sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
```

---

## 2. Compiling the Debug APK
```bash
cd TradeNov-Android
./gradlew assembleDebug
```
The resulting binary will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 3. Generating Release Android App Bundle (AAB)

To generate an optimized AAB for Google Play Store upload:
```bash
./gradlew bundleRelease
```
The bundle will be generated at:
`app/build/outputs/bundle/release/app-release.aab`

### Signing Configuration
In `app/build.gradle.kts`, configure your production release keystore:
```kotlin
signingConfigs {
    create("release") {
        storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
        storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
        keyAlias = System.getenv("KEY_ALIAS") ?: ""
        keyPassword = System.getenv("KEY_PASSWORD") ?: ""
    }
}
```

---

## 4. Backend Environment Switching

The app automatically switches between local emulator development and production via `BuildConfig`:
- **Local Dev / Android Emulator**: `http://10.0.2.2:4000/api/v1/`
- **Physical Device / Staging**: `http://<YOUR_LAN_IP>:4000/api/v1/`
- **Production**: `https://tradenov.com/api/v1/`
