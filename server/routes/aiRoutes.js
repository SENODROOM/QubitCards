const express = require('express');
const router = express.Router();
const { generateCards } = require('../controllers/aiController');

router.post('/generate', generateCards);

module.exports = router;
