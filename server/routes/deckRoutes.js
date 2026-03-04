// routes/deckRoutes.js
const express = require('express');
const router = express.Router();
const { getDecks, getDeck, createDeck, updateDeck, deleteDeck, resetProgress } = require('../controllers/deckController');
const { getCards, createCard, createManyCards } = require('../controllers/cardController');

router.get('/', getDecks);
router.post('/', createDeck);
router.get('/:id', getDeck);
router.put('/:id', updateDeck);
router.delete('/:id', deleteDeck);
router.post('/:id/reset', resetProgress);
router.get('/:deckId/cards', getCards);
router.post('/:deckId/cards', createCard);
router.post('/:deckId/cards/bulk', createManyCards);

module.exports = router;
