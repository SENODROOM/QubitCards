import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 35000,
  headers: { 'Content-Type': 'application/json' },
});

// ── Decks ────────────────────────────────────────────
export const getDecks = () => api.get('/decks');
export const getDeck = (id) => api.get(`/decks/${id}`);
export const createDeck = (data) => api.post('/decks', data);
export const updateDeck = (id, data) => api.put(`/decks/${id}`, data);
export const deleteDeck = (id) => api.delete(`/decks/${id}`);
export const resetDeckProgress = (id) => api.post(`/decks/${id}/reset`);

// ── Cards ────────────────────────────────────────────
export const getCards = (deckId) => api.get(`/decks/${deckId}/cards`);
export const createCard = (deckId, data) => api.post(`/decks/${deckId}/cards`, data);
export const createManyCards = (deckId, cards) => api.post(`/decks/${deckId}/cards/bulk`, { cards });
export const updateCard = (id, data) => api.put(`/cards/${id}`, data);
export const deleteCard = (id) => api.delete(`/cards/${id}`);
export const toggleMastered = (id) => api.patch(`/cards/${id}/mastered`);

// ── AI ───────────────────────────────────────────────
export const generateAICards = (data) => api.post('/ai/generate', data);

export default api;
