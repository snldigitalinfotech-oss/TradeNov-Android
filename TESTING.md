# TradeNov Android Testing Guide

This guide details the automated test suite and manual verification checklist for the TradeNov Android application.

---

## 1. Automated Unit Tests

Run all unit tests via Gradle:

```bash
export JAVA_HOME="/path/to/jdk-17"
export ANDROID_HOME="/path/to/android-sdk"

cd TradeNov-Android
./gradlew test
```

### Verified Test Cases
1. `testTraderLoginRoleRedirect`: Verifies that a user logging in with `role = TRADER` is routed to the Trader Dashboard (`NavigateToTraderHome`).
2. `testExpertLoginRoleRedirect`: Verifies that an approved expert logging in with `role = EXPERT` is routed to the Expert Dashboard (`NavigateToExpertDashboard`).
3. `testExpertRegistrationPendingApproval`: Verifies that a newly registered expert is flagged as `PENDING_APPROVAL` and routed to the `PendingApprovalScreen`.
4. `testTipUnlockFlow`: Validates atomic wallet deduction, tip unlock state update, transaction history generation, and VIP channel link generation.
5. `testWalletAddMoney`: Verifies wallet balance increment, credit transaction recording, and in-app payment notification.
6. `testExpertPublishTip`: Verifies new tip publication with live price snapshot, market segment mapping, and broadcast notification.
7. `testExpertFollowUpAction`: Verifies Target 1 Hit update, status change to `TARGET_HIT`, timeline step addition, and broadcast dispatch.
8. `testPlatformCommissionCalculation`: Verifies server-side 10% platform fee calculation (₹1,000 &rarr; ₹100 platform fee + ₹900 net expert earning).

---

## 2. Manual End-to-End QA Checklist

### A. Authentication & Onboarding
- [x] Splash screen displays TradeNov logo & tagline on orange background.
- [x] Onboarding screens swipe smoothly through 4 slides.
- [x] Circular orange Next button advances slides; Skip button jumps to Login.
- [x] Quick switch buttons on Login ("Auto Trader" / "Auto Expert") fill credentials.
- [x] Trader login navigates to Trader Home Dashboard.
- [x] Expert login navigates to Expert Analytics Desk.
- [x] Register allows switching between "Trader" and "Expert" account types.
- [x] Newly registered expert displays "Your expert account is under review" screen.

### B. Trader Panel
- [x] Header displays greeting, user name, wallet balance, and notification bell.
- [x] Live ticker strip displays quotes with green (+) and red (-) percentage changes.
- [x] Filter chips (Stocks, Index Options, Commodity, Crypto) filter tip listings.
- [x] Locked tip cards blur entry/targets with lock icon and "Unlock • ₹99".
- [x] Clicking locked tip opens detail screen with wallet deduction & 1-click VIP join links.
- [x] Unlocked tip reveals full entry bracket, T1/T2/T3, stop loss, and live P/L %.
- [x] Vertical execution timeline displays published, triggered, and target hit steps.
- [x] 1-Click "Join Telegram VIP" and "Join WhatsApp VIP" open respective applications.
- [x] Wallet screen displays available balance, Add Money dialog, and transaction history.
- [x] Subscriptions screen displays active passes with countdown timer.
- [x] Theme switcher in Account screen toggles between Light Mode and Dark Mode.

### C. Expert Panel
- [x] Expert Dashboard displays net available earnings, active subscribers, and win rate.
- [x] "Publish Tip" form allows one-click template selection (Options Buy, Swing Buy, etc.).
- [x] Entry range, target 1/2/3, stop loss, and commentary can be configured.
- [x] Real-time Social Broadcast toggles for Telegram VIP and WhatsApp VIP are active.
- [x] Manage Tips displays active running signals at the top.
- [x] 1-Tap action buttons (Target 1, Trail SL, SL Hit, Close) update signal status.
- [x] Earnings screen displays 10% platform commission fee and transaction breakdown.
