import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Sparkles, ChevronRight, Trash2, RefreshCw, CheckCircle, ArrowLeft } from 'lucide-react';
import { generateAICards, createDeck, createManyCards } from '../utils/api';
import Button from '../components/common/Button';

const DIFFICULTIES = [
  { key: 'easy', label: 'Easy', emoji: '🟢', desc: 'Basic facts & definitions' },
  { key: 'medium', label: 'Medium', emoji: '🟡', desc: 'Understanding & concepts' },
  { key: 'hard', label: 'Hard', emoji: '🔴', desc: 'Analysis & application' },
];

export default function GeneratePage() {
  const navigate = useNavigate();
  const [step, setStep] = useState('input'); // input | generating | preview | saving
  const [topic, setTopic] = useState('');
  const [cardCount, setCardCount] = useState(10);
  const [difficulty, setDifficulty] = useState('medium');
  const [cards, setCards] = useState([]);
  const [deckTitle, setDeckTitle] = useState('');
  const [error, setError] = useState('');

  const handleGenerate = async () => {
    if (!topic.trim()) { setError('Please enter a topic'); return; }
    setError(''); setStep('generating');
    try {
      const { data } = await generateAICards({ topic: topic.trim(), count: cardCount, difficulty });
      setCards(data.data);
      setDeckTitle(topic.trim().replace(/\b\w/g, c => c.toUpperCase()));
      setStep('preview');
    } catch (err) {
      setError(err.response?.data?.message || 'AI generation failed. Check your API key in server/.env');
      setStep('input');
    }
  };

  const handleSave = async () => {
    if (!deckTitle.trim()) { toast.error('Enter a deck title'); return; }
    setStep('saving');
    try {
      const { data: deckData } = await createDeck({ title: deckTitle, topic, isAiGenerated: true });
      await createManyCards(deckData.data._id, cards);
      toast.success(`Deck "${deckTitle}" saved with ${cards.length} cards!`);
      navigate(`/quiz/${deckData.data._id}`);
    } catch {
      toast.error('Failed to save deck');
      setStep('preview');
    }
  };

  return (
    <div style={{ maxWidth: 760, margin: '0 auto', padding: '32px 24px' }}>
      {/* Back */}
      <button onClick={() => step === 'preview' ? setStep('input') : navigate('/')}
        style={{ display: 'flex', alignItems: 'center', gap: 6, background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', fontWeight: 700, fontSize: 14, marginBottom: 24, fontFamily: 'var(--font-main)', padding: 0 }}>
        <ArrowLeft size={16} /> {step === 'preview' ? 'Back to Input' : 'Back to Home'}
      </button>

      {/* Input Step */}
      {step === 'input' && (
        <div className="fade-in-up">
          {/* Hero */}
          <div style={{ borderRadius: 'var(--radius-xl)', background: 'linear-gradient(135deg, var(--primary), #AB47BC)', padding: 36, marginBottom: 28, overflow: 'hidden', position: 'relative' }}>
            <div style={{ position: 'absolute', top: -40, right: -40, width: 160, height: 160, borderRadius: '50%', background: 'rgba(255,255,255,0.08)' }} />
            <div style={{ position: 'relative', zIndex: 1 }}>
              <div style={{ fontSize: 42, marginBottom: 12 }}>🤖</div>
              <h1 style={{ fontSize: 28, fontWeight: 900, color: 'white', marginBottom: 8 }}>AI Flashcard Generator</h1>
              <p style={{ color: 'rgba(255,255,255,0.85)', fontSize: 16, lineHeight: 1.6 }}>Type any topic and AI will generate a complete deck of flashcards instantly.</p>
            </div>
          </div>

          {/* Form */}
          <div style={{ background: 'white', borderRadius: 'var(--radius-lg)', padding: 28, boxShadow: 'var(--shadow-sm)', border: '1px solid var(--border)' }}>
            {/* Topic */}
            <div style={{ marginBottom: 24 }}>
              <label style={labelStyle}>What do you want to learn?</label>
              <div style={{ position: 'relative' }}>
                <input
                  value={topic}
                  onChange={e => { setTopic(e.target.value); setError(''); }}
                  onKeyDown={e => e.key === 'Enter' && handleGenerate()}
                  placeholder="e.g. Photosynthesis, World War II, Python basics, React Hooks..."
                  style={{ ...inputStyle, paddingLeft: 44, borderColor: error ? '#E53935' : undefined }}
                  autoFocus
                />
                <Sparkles size={18} style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--primary)', opacity: 0.7 }} />
              </div>
              {error && <p style={{ color: '#E53935', fontSize: 13, marginTop: 6, fontWeight: 600 }}>{error}</p>}
            </div>

            {/* Card Count */}
            <div style={{ marginBottom: 24 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
                <label style={labelStyle}>Number of Cards</label>
                <span style={{ background: 'var(--primary-bg)', color: 'var(--primary)', fontWeight: 800, fontSize: 16, padding: '4px 14px', borderRadius: 20 }}>{cardCount}</span>
              </div>
              <input type="range" min={5} max={20} value={cardCount} onChange={e => setCardCount(Number(e.target.value))}
                style={{ width: '100%', accentColor: 'var(--primary)', height: 6, cursor: 'pointer' }} />
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 4 }}>
                <span style={{ fontSize: 12, color: 'var(--text-light)' }}>5</span>
                <span style={{ fontSize: 12, color: 'var(--text-light)' }}>20</span>
              </div>
            </div>

            {/* Difficulty */}
            <div style={{ marginBottom: 28 }}>
              <label style={labelStyle}>Difficulty Level</label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 10 }}>
                {DIFFICULTIES.map(d => (
                  <button key={d.key} onClick={() => setDifficulty(d.key)}
                    style={{ padding: '12px 8px', borderRadius: 12, border: `2px solid ${difficulty === d.key ? 'var(--primary)' : 'var(--border)'}`, background: difficulty === d.key ? 'var(--primary-bg)' : 'transparent', cursor: 'pointer', textAlign: 'center', transition: 'all 0.2s', fontFamily: 'var(--font-main)' }}>
                    <div style={{ fontSize: 20, marginBottom: 4 }}>{d.emoji}</div>
                    <div style={{ fontSize: 13, fontWeight: 800, color: difficulty === d.key ? 'var(--primary)' : 'var(--text)' }}>{d.label}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{d.desc}</div>
                  </button>
                ))}
              </div>
            </div>

            <Button fullWidth size="lg" onClick={handleGenerate} disabled={!topic.trim()}>
              <Sparkles size={18} /> Generate {cardCount} Flashcards
            </Button>
          </div>
        </div>
      )}

      {/* Generating */}
      {step === 'generating' && (
        <div className="fade-in" style={{ textAlign: 'center', padding: '80px 20px' }}>
          <div style={{ fontSize: 72, marginBottom: 24, animation: 'pulse 1.5s ease infinite' }}>🤖</div>
          <div className="spinner" style={{ margin: '0 auto 24px' }} />
          <h2 style={{ fontSize: 26, fontWeight: 800, marginBottom: 10 }}>Generating your cards...</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: 16 }}>AI is crafting {cardCount} flashcards about<br /><strong>"{topic}"</strong></p>
        </div>
      )}

      {/* Preview */}
      {step === 'preview' && (
        <div className="fade-in-up">
          {/* Header */}
          <div style={{ background: 'white', borderRadius: 'var(--radius-lg)', padding: 24, marginBottom: 20, boxShadow: 'var(--shadow-sm)', border: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
              <div style={{ fontSize: 24 }}>✅</div>
              <div>
                <h2 style={{ fontSize: 20, fontWeight: 800 }}>{cards.length} cards generated!</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: 14 }}>Review, edit, or remove cards before saving.</p>
              </div>
            </div>
            <label style={labelStyle}>Deck Title</label>
            <input value={deckTitle} onChange={e => setDeckTitle(e.target.value)} placeholder="Enter deck name" style={inputStyle} />
          </div>

          {/* Cards list */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 20 }}>
            {cards.map((card, i) => (
              <div key={i} style={{ background: 'white', borderRadius: 'var(--radius-md)', padding: 16, border: '1px solid var(--border)', display: 'flex', gap: 14, alignItems: 'flex-start', animation: `fadeInUp 0.3s ease ${i * 0.03}s forwards`, opacity: 0 }}>
                <div style={{ width: 28, height: 28, borderRadius: 8, background: 'var(--primary)', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, fontWeight: 800, flexShrink: 0 }}>{i + 1}</div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <p style={{ fontWeight: 700, marginBottom: 6, fontSize: 14 }}>{card.question}</p>
                  <hr style={{ border: 'none', borderTop: '1px solid var(--border)', margin: '8px 0' }} />
                  <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>{card.answer}</p>
                </div>
                <button onClick={() => setCards(prev => prev.filter((_, idx) => idx !== i))}
                  style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#E53935', padding: 4, borderRadius: 6, display: 'flex', alignItems: 'center' }}>
                  <Trash2 size={15} />
                </button>
              </div>
            ))}
          </div>

          {/* Save actions */}
          <div style={{ display: 'flex', gap: 12, position: 'sticky', bottom: 20 }}>
            <Button variant="outline" onClick={() => { setStep('input'); setCards([]); }} style={{ flex: 1 }}>
              <RefreshCw size={15} /> Regenerate
            </Button>
            <Button onClick={handleSave} disabled={cards.length === 0} style={{ flex: 2 }}>
              <CheckCircle size={15} /> Save & Study ({cards.length} cards)
            </Button>
          </div>
        </div>
      )}

      {/* Saving */}
      {step === 'saving' && (
        <div className="fade-in" style={{ textAlign: 'center', padding: '80px 20px' }}>
          <div style={{ fontSize: 64, marginBottom: 20 }}>💾</div>
          <div className="spinner" style={{ margin: '0 auto 20px' }} />
          <h2 style={{ fontSize: 22, fontWeight: 800 }}>Saving your deck...</h2>
        </div>
      )}
    </div>
  );
}

const labelStyle = { display: 'block', fontSize: 13, fontWeight: 800, marginBottom: 8, color: 'var(--primary)', letterSpacing: 0.5 };
const inputStyle = {
  width: '100%', padding: '12px 14px', borderRadius: 12,
  border: '2px solid var(--border)', outline: 'none',
  fontSize: 15, fontFamily: 'var(--font-main)', fontWeight: 500,
  transition: 'border-color 0.2s', boxSizing: 'border-box', background: 'var(--surface)',
};
