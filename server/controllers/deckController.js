const Deck = require('../models/Deck');
const Card = require('../models/Card');

// GET all decks with card counts
exports.getDecks = async (req, res) => {
  try {
    const decks = await Deck.find().sort({ createdAt: -1 }).populate('cardCount');
    // Get mastered counts
    const deckIds = decks.map(d => d._id);
    const masteredCounts = await Card.aggregate([
      { $match: { deck: { $in: deckIds }, isMastered: true } },
      { $group: { _id: '$deck', count: { $sum: 1 } } }
    ]);
    const masteredMap = {};
    masteredCounts.forEach(m => { masteredMap[m._id.toString()] = m.count; });

    const result = decks.map(d => ({
      ...d.toObject(),
      cardCount: d.cardCount || 0,
      masteredCount: masteredMap[d._id.toString()] || 0,
    }));
    res.json({ success: true, data: result });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

// GET single deck
exports.getDeck = async (req, res) => {
  try {
    const deck = await Deck.findById(req.params.id).populate('cardCount');
    if (!deck) return res.status(404).json({ success: false, message: 'Deck not found' });
    const masteredCount = await Card.countDocuments({ deck: deck._id, isMastered: true });
    res.json({ success: true, data: { ...deck.toObject(), cardCount: deck.cardCount || 0, masteredCount } });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

// POST create deck
exports.createDeck = async (req, res) => {
  try {
    const { title, description, topic, isAiGenerated, color, emoji } = req.body;
    const deck = await Deck.create({ title, description, topic, isAiGenerated, color, emoji });
    res.status(201).json({ success: true, data: { ...deck.toObject(), cardCount: 0, masteredCount: 0 } });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// PUT update deck
exports.updateDeck = async (req, res) => {
  try {
    const deck = await Deck.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
    if (!deck) return res.status(404).json({ success: false, message: 'Deck not found' });
    res.json({ success: true, data: deck });
  } catch (err) {
    res.status(400).json({ success: false, message: err.message });
  }
};

// DELETE deck and all its cards
exports.deleteDeck = async (req, res) => {
  try {
    const deck = await Deck.findByIdAndDelete(req.params.id);
    if (!deck) return res.status(404).json({ success: false, message: 'Deck not found' });
    await Card.deleteMany({ deck: req.params.id });
    res.json({ success: true, message: 'Deck deleted' });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

// POST reset deck progress
exports.resetProgress = async (req, res) => {
  try {
    await Card.updateMany({ deck: req.params.id }, { isMastered: false });
    res.json({ success: true, message: 'Progress reset' });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
