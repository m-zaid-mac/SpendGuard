# SpendGuard

An Android spend-management app that categorizes transactions and flags anomalies —
built as a focused portfolio project mirroring the core loop of Ramp's product
(authorizing/categorizing spend, flagging risk) using the stack listed in Ramp's
Android intern JD: Kotlin, Jetpack Compose, MVVM, coroutines, Flow, Gradle.

## What it does

- **Dashboard** — total spend, spend by category, count of flagged transactions
- **Transaction list** — search by merchant, filter to flagged-only
- **Transaction detail** — shows exactly *why* a transaction was flagged, lets you
  manually recategorize a transaction
- **Biometric lock** — app content is gated behind device biometrics/PIN on launch

## Architecture

MVVM, single-activity, single-source-of-truth repository:

```
Compose UI  →  ViewModel (StateFlow)  →  Repository  →  Room DB
                                                     ↘  Categorization engine
                                                     ↘  Risk engine
```

- `domain/CategorizationEngine.kt` and `domain/RiskEngine.kt` are **plain Kotlin,
  zero Android imports** — deliberately, so the business logic is testable with
  vanilla JUnit and portable if it ever needed to move to a backend service.
- `CategorizationEngine` is a merchant-name regex rule engine, not an ML model.
  Real-world spend categorization systems (Ramp's included) lean on merchant
  category codes and rules for the bulk of transactions and only reach for a
  model on the ambiguous long tail — this project makes that same call rather
  than overclaiming an ML pipeline it doesn't need yet.
- `RiskEngine` runs four independent checks over a transaction batch:
  unusual amount vs. category average, duplicate charges, velocity bursts on
  a single card, and large first-time charges to a new merchant.
- No DI framework (no Hilt/Dagger) — manual provision via the `Application`
  class. A deliberate scope call for an app this size, not an oversight.

## Data

There's no backend. `app/src/main/assets/mock_transactions.json` seeds a local
Room database on first launch with 71 realistic mock transactions, including
four planted anomalies (a duplicate charge, a velocity burst, an outsized
category spend, and a large new-merchant charge) so the risk engine has
something real to catch on first run.

**What I'd build next with more time:** a FastAPI backend (matching the JD's
backend nice-to-have) serving transactions over a real API, with the
categorization/risk logic optionally promoted to a shared service both the
app and a web dashboard could call.

## Running it

1. Open in Android Studio (Koala or newer)
2. Let Gradle sync — it will regenerate the wrapper
3. Run on an emulator or device with API 26+
4. If the emulator has no biometric/PIN enrolled, the lock screen fails open
   automatically so you're not stranded

## Tests

```
./gradlew test
```

`CategorizationEngineTest` and `RiskEngineTest` cover both engines directly,
independent of Android/Room/Compose.

## Stack

Kotlin · Jetpack Compose · Material3 · MVVM · StateFlow/Flow · Coroutines ·
Room · Navigation Compose · AndroidX Biometric · kotlinx.serialization · JUnit

## Author

**Mohammad Zaid**

- GitHub: [@m-zaid-mac](https://github.com/m-zaid-mac)
- LinkedIn: [mohammad-zaid](https://www.linkedin.com/in/mohammad-zaid-6a360b276/)
- Email: zaid.m@northeastern.edu
