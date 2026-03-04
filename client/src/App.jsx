import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/common/Navbar';
import HomePage from './pages/HomePage';
import GeneratePage from './pages/GeneratePage';
import QuizPage from './pages/QuizPage';
import ManagePage from './pages/ManagePage';

export default function App() {
  return (
    <div style={{ minHeight: '100vh', background: 'var(--surface)' }}>
      <Navbar />
      <main style={{ paddingTop: '72px' }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/generate" element={<GeneratePage />} />
          <Route path="/quiz/:deckId" element={<QuizPage />} />
          <Route path="/manage/:deckId" element={<ManagePage />} />
        </Routes>
      </main>
    </div>
  );
}
