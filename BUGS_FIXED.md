# TradeNov Android - Bugs & Technical Issues Resolved

This document details the issues identified during the implementation, compilation, and testing cycles, along with their root causes and resolutions.

---

### Bug 1: Missing Unit Test Main Dispatcher Initialization
- **Symptom**: `IllegalStateException: Module with the Main dispatcher had failed to initialize` when running `AuthViewModel` coroutine tests.
- **Root Cause**: In unit tests, Android `Dispatchers.Main` is not available by default unless explicitly configured with `kotlinx-coroutines-test`.
- **Resolution**: Configured `StandardTestDispatcher()` inside `@Before` with `Dispatchers.setMain(testDispatcher)` and `@After` with `Dispatchers.resetMain()`, along with `testDispatcher.scheduler.advanceUntilIdle()`.

---

### Bug 2: Mockito Suspend Function Continuation Mismatch
- **Symptom**: `InvalidUseOfMatchersException` when attempting to mock Retrofit `suspend fun login()` with Mockito matchers.
- **Root Cause**: Kotlin suspend functions under the hood contain a hidden `Continuation` parameter as the last argument, which conflicts with standard `Mockito.any()` reflection matchers.
- **Resolution**: Created `FakeTradeNovApiService` implementing `TradeNovApiService`. This decoupled the unit tests from network sockets and reflection, enabling instant test execution (5s build time).

---

### Bug 3: Missing Compose `dp` Import in Expert Panel
- **Symptom**: `Unresolved reference: dp` on line 43 of `ExpertMainScreen.kt`.
- **Root Cause**: `tonalElevation = 8.dp` was specified in `NavigationBar` without the explicit import for `androidx.compose.ui.unit.dp`.
- **Resolution**: Added `import androidx.compose.ui.unit.dp` to `ExpertMainScreen.kt`.

---

### Bug 4: Inaccessible `sessionManager` in Repository
- **Symptom**: `Cannot access 'sessionManager': it is private in 'TradeNovRepository'` in `TraderHomeScreen.kt`.
- **Root Cause**: `sessionManager` was declared as `private val sessionManager` in `TradeNovRepository`, preventing UI screens from observing reactive session preferences.
- **Resolution**: Changed visibility to public `val sessionManager: SessionManager`.

---

### Bug 5: Case-Sensitive APK Serving
- **Symptom**: Earlier user report indicated `tradenov.apk.html failed - no file` during web downloads.
- **Root Cause**: Case mismatch between file paths in web downloads directory.
- **Resolution**: Copied fresh compiled `app-debug.apk` directly to all standard download paths:
  - `TradeNov-Android/TradeNov-debug.apk`
  - `apps/web/public/downloads/tradenov.apk`
  - `apps/web/public/tradenov.apk`
  - `tradenov.apk` (root)
