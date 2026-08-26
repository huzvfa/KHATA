# Khata — Personal Finance Tracker (PKR)

A native Android app (Kotlin + Jetpack Compose) for tracking income, expenses,
budgets, and savings goals in PKR — with automatic transaction capture from
Meezan Bank SMS alerts.

## What this does and doesn't do

- ✅ Everything is stored **locally on your phone** (Room/SQLite database). No
  server, no cloud account, no internet permission requested at all.
- ✅ When a Meezan transaction alert SMS arrives, a `BroadcastReceiver` reads it
  on-device, tries to extract the amount/type, and logs it automatically.
- ✅ If an SMS looks like a bank alert but the app can't confidently parse it,
  it's queued in the **"Review SMS"** tab (bell icon, top right) instead of
  being silently dropped — you add it in one tap.
- ❌ This is **not** connected to Meezan's servers and never touches your
  online banking credentials, PIN, or OTP. No such official free API exists
  for individual customers — anything claiming otherwise should be treated as
  a scam.
- ⚠️ I don't have a live sample of your actual Meezan SMS wording, so
  `SmsParser.kt` is built on the phrasing patterns common to Pakistani bank
  alerts (`Rs./PKR`, "debited", "credited", "Avl Bal"). It should catch most
  real alerts, but if some come through as "needs review" instead of
  auto-categorized, send me one real SMS (redact your account number/OTP) and
  I'll tighten the regex for your exact bank's wording.

## How to get a compiled .apk — two options

### Option A: Let GitHub build it for you (no software install, ~5 min)

This project includes `.github/workflows/build-apk.yml`, which makes GitHub's
own servers compile the APK — you just upload the code.

1. Create a free account at https://github.com if you don't have one.
2. Create a new (public or private) repository, e.g. `khata-app`.
3. On your computer, unzip this project, then in that folder run:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/khata-app.git
   git push -u origin main
   ```
4. On GitHub, open your repo → the **Actions** tab. A "Build APK" run will
   already be in progress (it starts automatically on push).
5. When it finishes (green check, a couple of minutes), click into that run
   → under **Artifacts**, download **khata-debug-apk** — that's a zip
   containing `app-debug.apk`. Transfer it to your phone and install it
   (you'll need to allow "install from unknown sources" once).

No Android Studio, no local SDK, nothing to install — GitHub's servers do the
actual compiling.

### Option B: Build it yourself in Android Studio

1. Install **Android Studio** (free): https://developer.android.com/studio
2. Open this folder (`Khata/`) as a project — "Open" → select the `Khata`
   folder. Android Studio will detect there's no Gradle wrapper and offer to
   generate one automatically; accept it.
3. Let it sync (downloads Gradle + dependencies — needs internet, first time
   only).
4. Plug in your Android phone via USB with **Developer Options → USB
   debugging** enabled, and click the green ▶ Run button — this installs a
   debug build directly.
   - Or: **Build → Build Bundle(s)/APK(s) → Build APK(s)** to get an
     installable `.apk` file you can transfer/sideload.
5. On first launch, grant the SMS permission when asked — this is what lets
   the auto-detect feature work. If you skip it, everything still works, you
   just add every transaction manually via the **+** button.

## Where things live in the code

| Feature | File |
|---|---|
| Currency formatting (PKR) | `util/CurrencyUtils.kt` |
| SMS matching + parsing logic | `sms/SmsParser.kt` |
| SMS receiver (fires on incoming SMS) | `sms/SmsReceiver.kt` |
| Database tables | `data/*.kt` |
| Dashboard / balance / recent activity | `ui/DashboardScreen.kt` |
| Budgets by category | `ui/BudgetsScreen.kt` |
| Savings goals | `ui/GoalsScreen.kt` |
| Monthly reports | `ui/ReportsScreen.kt` |
| Unparsed-SMS review queue | `ui/ReviewSmsScreen.kt` |

## Tuning the SMS parser to your exact alerts

Open `sms/SmsParser.kt`. Two things you may need to adjust:

1. `KNOWN_SENDERS` — the SMS sender ID Meezan uses on your SIM (check an old
   alert SMS to see the exact sender name/number and add it if it's not
   already listed).
2. The regex patterns (`amountPattern`, `balancePattern`, `debitWords`,
   `creditWords`) — edit these to match your alerts' exact wording if
   messages keep landing in "Review SMS" instead of auto-categorizing.

## Known limitations (typical of this whole approach, not just this app)

- SMS-based tracking only catches transactions that trigger an SMS alert —
  if you ever disable Meezan SMS alerts, this stops working.
- Some Android phones (especially aggressive battery-optimization on Xiaomi/
  Oppo/Vivo/Huawei-based ROMs) can kill background broadcast receivers. If
  transactions stop auto-logging, check **Settings → Apps → Khata → Battery →
  Unrestricted**.
- Android 14+ requires the SMS permission to be granted at runtime and can
  auto-revoke it from unused apps — open the app occasionally to keep it
  active.
