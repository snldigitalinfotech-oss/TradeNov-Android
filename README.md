# TradeNov Android Mobile Application

[![Platform](https://img.shields.io/badge/Platform-Android%2024%2B-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20MVVM-orange.svg)](https://developer.android.com/topic/architecture)
[![Build](https://img.shields.io/badge/Build-Passing%20(8%2F8%20Tests)-success.svg)](https://github.com)

**TradeNov** (*Navigate. Trade. Grow.*) is a financial technology application and verified trading tips marketplace for **Stock Market, Futures & Options, Commodity, Forex, and Cryptocurrency**.

Built from the ground up following the approved mobile UI/UX visual language (vibrant orange accent `#FF7A00`, crisp white backgrounds, soft elevations, rounded card hierarchy, and minimal navigation), TradeNov delivers a high-conviction mobile experience for both retail traders and independent technical analysts.

---

## 📱 Application Panels & Role-Based Redirection

TradeNov utilizes a **unified authentication portal**. After login, the application automatically inspects the user's role and routes them accordingly:

1. **TRADER PANEL (`role = TRADER`)**:
   - **Trader Home Dashboard**: Live market ticker (Nifty 50, BankNifty, Sensex, BTC, ETH, Gold), quick actions (Add Money, Browse Tips, Pay-Per-Tip from ₹99, Watchlist), category filter chips, today's top tips, and featured technical analysts.
   - **Tips Marketplace**: Filter by market (Stocks, Index Options, Stock Options, Commodity, Forex, Crypto), trade type (Intraday, Swing, Delivery), and status (Running, Target Hit, Closed).
   - **Tip Details & Execution Brackets**: Real-time CMP vs captured entry price, target 1/2/3, stop loss, live P/L indicator, and vertical execution timeline.
   - **Social VIP Broadcast Integration**: Instant 1-click access to official VIP Telegram channels and WhatsApp VIP communities for real-time target alerts.
   - **Wallet & Add Money**: Available balance, instant UPI QR & Gateway top-up, withdrawal requests, and immutable transaction ledger.
   - **Subscriptions**: Expert monthly/weekly VIP passes with countdown timer.
   - **Notifications & Deep Linking**: Real-time push alerts on target hits, stop loss, and market follow-ups.

2. **EXPERT PANEL (`role = EXPERT`)**:
   - **Expert Dashboard Desk**: Net available earnings, active subscribers count, win rate %, and active running signals count.
   - **Publish Trading Signal**: One-click quick templates (Options Buy, Swing Cash Buy, Commodity Buy), auto CMP display, entry range brackets, target 1/2/3, stop loss, analyst commentary, and real-time social broadcast toggles.
   - **Manage Active Signals**: Instant 1-tap follow-up updates (🎯 Target 1 Hit, 🎯 Final Target Hit, 💰 Partial Profit / Trailing SL, 🛑 Stop Loss Hit, 🏁 Close Tip) with custom broadcast note synchronization to Telegram & WhatsApp.
   - **Earnings & Payouts**: Gross earnings vs 10% platform commission ledger with direct NEFT/IMPS/UPI payout requests.

---

## 🛠️ Technology Stack & Architecture

- **Language**: Kotlin 1.9.20
- **UI Toolkit**: Jetpack Compose & Material 3
- **Design System**: TradeNov Financial Tokens (Orange `#FF7A00`, Profit Green `#10B981`, Loss Red `#EF4444`, Light `#FFFFFF`, Dark `#0D1117`)
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Asynchronous Flow**: Kotlin Coroutines + StateFlow
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0 + Logging Interceptors
- **Data & Security**: Encrypted Session Manager + SharedPreferences + Immutable Audit Records
- **Image Loading**: Coil Compose 2.5.0
- **Testing**: JUnit 4 + Coroutines Test + Mockito

---

## 🚀 Building & Running Locally

### Prerequisites
- JDK 17 (e.g. Zulu JDK 17 or OpenJDK 17)
- Android SDK 34 (`platforms/android-34` & `build-tools/33.0.1+`)

### 1. Build Debug APK
```bash
export JAVA_HOME="/path/to/jdk-17"
export ANDROID_HOME="/path/to/android-sdk"

./gradlew clean
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Test Suite
```bash
./gradlew test
```
All unit tests validate trader login, expert role redirection, pending approval flows, tip unlocking, wallet balance deductions, expert publishing, and 10% platform commission calculation.

---

## 🔒 Security & Statutory Disclaimers
- All API tokens and sensitive session credentials are saved via encrypted storage abstractions.
- Live market prices are proxied securely through the backend server.
- All trading content includes statutory market risk disclaimers and educational disclosure notes. TradeNov is NOT a SEBI-registered advisory entity.
