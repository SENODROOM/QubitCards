const mongoose = require('mongoose');

const CardSchema = new mongoose.Schema({
  deck: { type: mongoose.Schema.Types.ObjectId, ref: 'Deck', required: true, index: true },
  question: { type: String, required: true, trim: true },
  answer: { type: String, required: true, trim: true },
  isMastered: { type: Boolean, default: false },
  order: { type: Number, default: 0 },
}, {
  timestamps: true,
});

module.exports = mongoose.model('Card', CardSchema);
