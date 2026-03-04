# ⚡ FlashCard AI — MERN Stack Web App

A full-stack AI-powered flashcard study app built with **MongoDB, Express, React, Node.js** and **Google Gemini** (free API).

---

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ installed
- MongoDB running locally OR a free MongoDB Atlas cluster

### Step 1 — Clone & Install
```bash
cd flashcard-mern
npm run install-all
```

### Step 2 — Configure Environment
```bash
cp server/.env.example server/.env
```
Edit `server/.env`:
```env
MONGODB_URI=mongodb://localhost:27017/flashcardai
GEMINI_API_KEY=YOUR_KEY_HERE
PORT=5000
CLIENT_URL=http://localhost:3000
```

**Get FREE Gemini API key:** https://aistudio.google.com/app/apikey  
**Get FREE MongoDB Atlas:** https://cloud.mongodb.com (free 512MB cluster)

### Step 3 — Run
```bash
npm run dev
```
Opens:
- 🌐 Frontend: http://localhost:3000
- 🔧 Backend: http://localhost:5000

---

## 📁 Project Structure

```
flashcard-mern/
├── package.json              # Root scripts (concurrently)
│
├── server/
│   ├── index.js              # Express app entry
│   ├── .env.example          # Environment template
│   ├── config/
│   │   └── db.js             # MongoDB connection
│   ├── models/
│   │   ├── Deck.js           # Deck schema
│   │   └── Card.js           # Card schema
│   ├── controllers/
│   │   ├── deckController.js # Deck CRUD logic
│   │   ├── cardController.js # Card CRUD logic
│   │   └── aiController.js   # Gemini AI integration
│   └── routes/
│       ├── deckRoutes.js     # /api/decks
│       ├── cardRoutes.js     # /api/cards
│       └── aiRoutes.js       # /api/ai
│
└── client/
    ├── public/index.html
    └── src/
        ├── App.jsx           # Root + routing
        ├── index.js          # Entry point
        ├── utils/
        │   └── api.js        # Axios API client
        ├── styles/
        │   └── global.css    # Theme variables + global styles
        ├── components/
        │   ├── common/
        │   │   ├── Navbar.jsx
        │   │   ├── Button.jsx
        │   │   └── Modal.jsx
        │   ├── deck/
        │   │   └── DeckCard.jsx
        │   └── cards/
        │       └── FlipCard.jsx
        └── pages/
            ├── HomePage.jsx     # Deck list + stats
            ├── GeneratePage.jsx # AI generation flow
            ├── QuizPage.jsx     # Study mode
            └── ManagePage.jsx   # Card CRUD
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/decks | List all decks |
| POST | /api/decks | Create deck |
| GET | /api/decks/:id | Get deck |
| PUT | /api/decks/:id | Update deck |
| DELETE | /api/decks/:id | Delete deck + cards |
| POST | /api/decks/:id/reset | Reset progress |
| GET | /api/decks/:deckId/cards | Get cards for deck |
| POST | /api/decks/:deckId/cards | Add card |
| POST | /api/decks/:deckId/cards/bulk | Add many cards |
| PUT | /api/cards/:id | Update card |
| DELETE | /api/cards/:id | Delete card |
| PATCH | /api/cards/:id/mastered | Toggle mastered |
| POST | /api/ai/generate | Generate AI cards |

---

## 🧱 Tech Stack

| Layer | Tech |
|---|---|
| Frontend | React 18, React Router v6 |
| Styling | CSS Variables, Framer Motion |
| HTTP Client | Axios |
| Notifications | React Hot Toast |
| Backend | Node.js, Express 4 |
| Database | MongoDB + Mongoose |
| AI | Google Gemini 1.5 Flash (FREE) |
| Dev Tools | Nodemon, Concurrently |

---

## 🎨 Features

- 🤖 **AI Generation** — type any topic, get a full deck instantly
- 🃏 **3D Card Flip** — smooth CSS 3D flip animation
- 📊 **Progress Tracking** — mastered cards, per-deck stats
- ✏️ **Full CRUD** — add, edit, delete cards & decks
- 🔍 **Search** — filter cards by question/answer
- ⌨️ **Keyboard shortcuts** — Arrow keys, F to flip
- 🌈 **Purple theme** — consistent design system with CSS variables
- 📱 **Responsive** — works on mobile & desktop
