# ⚡ FlashCard AI — Android App

An AI-powered flashcard study app built with **Kotlin + Jetpack Compose**, using **Google Gemini** (free API) to auto-generate flashcard decks on any topic.

---

## 📱 Features

| Feature | Description |
|---|---|
| 🤖 AI Generation | Type any topic → Gemini generates flashcards instantly |
| 🃏 3D Card Flip | Smooth flip animation to reveal answers |
| 📊 Progress Tracking | Track mastered cards per deck |
| ✏️ Full CRUD | Add, edit, delete cards and decks |
| 🌙 Dark Mode | Automatic dark/light theme |
| 💾 Offline Storage | Room DB — works without internet after generation |
| 🎯 Quiz Results | Session stats on completion |

---

## 🚀 Getting Started

### Step 1 — Get a FREE Gemini API Key

1. Visit **https://aistudio.google.com/app/apikey**
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Copy the key (it's free, no credit card needed)

### Step 2 — Add Your API Key

Open `app/build.gradle` and replace:

```groovy
buildConfigField "String", "GEMINI_API_KEY", '"YOUR_GEMINI_API_KEY_HERE"'
```

with:

```groovy
buildConfigField "String", "GEMINI_API_KEY", '"AIza...your_actual_key..."'
```

### Step 3 — Build & Run

```bash
# Open in Android Studio (Hedgehog or newer)
# Sync Gradle → Run on device/emulator (API 24+)
```

---

## 🏗️ Project Structure

```
FlashCardAI/
├── app/src/main/java/com/flashcardai/
│   ├── FlashCardApp.kt              # Hilt Application class
│   ├── MainActivity.kt              # Entry point
│   │
│   ├── data/
│   │   ├── local/
│   │   │   ├── FlashCardDatabase.kt # Room database
│   │   │   ├── dao/Daos.kt          # DAO interfaces
│   │   │   └── entity/Entities.kt   # Room entities
│   │   ├── remote/
│   │   │   └── GeminiApiService.kt  # Retrofit + Gemini models
│   │   └── repository/
│   │       ├── AiRepository.kt      # Gemini AI calls + parsing
│   │       └── FlashCardRepository.kt # Local DB operations
│   │
│   ├── di/
│   │   └── AppModule.kt             # Hilt DI modules (DB + Network)
│   │
│   ├── domain/
│   │   └── model/Models.kt          # Domain models (Deck, FlashCard...)
│   │
│   ├── presentation/
│   │   ├── navigation/NavHost.kt    # Compose Navigation
│   │   ├── theme/
│   │   │   ├── Theme.kt             # Material3 theme + colors
│   │   │   └── Typography.kt        # Text styles
│   │   └── screens/
│   │       ├── home/                # Deck list screen
│   │       ├── generate/            # AI generation flow
│   │       ├── quiz/                # Study/flip card screen
│   │       └── manage/              # CRUD cards screen
│   │
│   └── utils/
│       └── Resource.kt              # Success/Error/Loading wrapper
│
└── res/
    ├── values/strings.xml
    ├── values/themes.xml
    ├── values/colors.xml
    └── xml/network_security_config.xml
```

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Navigation | Compose Navigation |
| DI | Hilt |
| Database | Room |
| Networking | Retrofit2 + OkHttp3 |
| AI | Google Gemini 1.5 Flash (FREE) |
| Architecture | MVVM + Clean Architecture |
| State | StateFlow + collectAsStateWithLifecycle |

---

## 🔑 Free API Limits (Gemini)

- **15 requests/minute** — more than enough for a flashcard app
- **1 million tokens/day free**
- No credit card required
- Get key at: https://aistudio.google.com/app/apikey

---

## 📐 Architecture Overview

```
UI (Compose Screens)
    ↓ observes StateFlow
ViewModels (Hilt-injected)
    ↓ calls
Repositories (data layer)
    ↓                ↓
Room DB          Gemini API (Retrofit)
```

---

## 🛠️ Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9.10
- Java 17

---

## 📸 Screens

1. **Home** — Deck list with progress bars, stats banner, delete/edit options
2. **AI Generate** — Topic input → card count/difficulty → AI generation → preview → save
3. **Quiz** — 3D flip cards, mastered tracking, progress bar, session results
4. **Manage** — CRUD interface for all cards in a deck
