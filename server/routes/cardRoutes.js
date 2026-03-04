const express = require('express');
const router = express.Router();
const { updateCard, deleteCard, toggleMastered } = require('../controllers/cardController');

router.put('/:id', updateCard);
router.delete('/:id', deleteCard);
router.patch('/:id/mastered', toggleMastered);

module.exports = router;
