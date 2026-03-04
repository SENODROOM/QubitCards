const Card = require('../models/Card');

exports.getCards = async (req, res) => {
  try {
    const cards = await Card.find({ deck: req.params.deckId }).sort({ order: 1, createdAt: 1 });
    res.json({ success: true, data: cards });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.createCard = async (req, res) => {
  try {
    const { question, answer } = req.body;
    const card = await Card.create({ deck: req.params.deckId, question, answer });
    res.status(201).json({ success: true, data: card });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.createManyCards = async (req, res) => {
  try {
    const { cards } = req.body;
    const docs = cards.map((c, i) => ({ deck: req.params.deckId, question: c.question, answer: c.answer, order: i }));
    const created = await Card.insertMany(docs);
    res.status(201).json({ success: true, data: created, count: created.length });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.updateCard = async (req, res) => {
  try {
    const card = await Card.findByIdAndUpdate(req.params.id, req.body, { new: true });
    if (!card) return res.status(404).json({ success: false, message: 'Card not found' });
    res.json({ success: true, data: card });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

exports.deleteCard = async (req, res) => {
  try {
    const card = await Card.findByIdAndDelete(req.params.id);
    if (!card) return res.status(404).json({ success: false, message: 'Card not found' });
    res.json({ success: true, message: 'Card deleted' });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.toggleMastered = async (req, res) => {
  try {
    const card = await Card.findById(req.params.id);
    if (!card) return res.status(404).json({ success: false, message: 'Card not found' });
    card.isMastered = !card.isMastered;
    await card.save();
    res.json({ success: true, data: card });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
