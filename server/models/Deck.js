const mongoose = require('mongoose');

const DeckSchema = new mongoose.Schema({
  title: { type: String, required: true, trim: true, maxlength: 100 },
  description: { type: String, trim: true, maxlength: 500, default: '' },
  topic: { type: String, trim: true, default: '' },
  isAiGenerated: { type: Boolean, default: false },
  color: { type: String, default: '#6C63FF' },
  emoji: { type: String, default: '📖' },
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true },
});

// Virtual: card count
DeckSchema.virtual('cardCount', {
  ref: 'Card',
  localField: '_id',
  foreignField: 'deck',
  count: true,
});

module.exports = mongoose.model('Deck', DeckSchema);
