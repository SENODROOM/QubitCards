import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft, ChevronLeft, ChevronRight, Eye, CheckCircle, RotateCcw, Home, Trophy } from 'lucide-react';
import { getDeck, getCards, toggleMastered, resetDeckProgress } from '../utils/api';
import FlipCard from '../components/cards/FlipCard';
import Button from '../components/common/Button';

export default function QuizPage() {
  const { deckId } = useParams();
  const navigate = useNavigate();
  const [deck, setDeck] = useState(null);
  const [cards, setCards] = useState([]);
  const [current, setCurrent] = useState(0);
  const [flipped, setFlipped] = useState(false);
  const [loading, setLoading] = useState(true);
  const [completed, setCompleted] = useState(false);
  const [sessionMastered, setSessionMastered] = useState(new Set());

  const load = useCallback(async () => {
    try {
      const [deckRes, cardsRes] = await Promise.all([getDeck(deckId), getCards(deckId)]);
      setDeck(deckRes.data.data);
      const shuffled = [...cardsRes.data.data].sort(() => Math.random() - 0.5);
      setCards(shuffled);
    } catch { toast.error('Failed to load deck'); navigate('/'); }
    finally { setLoading(false); }
  }, [deckId, navigate]);

  useEffect(() => { load(); }, [load]);

  // Keyboard navigation
  useEffect(() => {
    const handler = (e) => {
      if (e.key === 'ArrowRight' || e.key === ' ') { e.preventDefault(); handleNext(); }
      if (e.key === 'ArrowLeft') { e.preventDefault(); handlePrev(); }
      if (e.key === 'f' || e.key === 'F') setFlipped(f => !f);
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  });

  const card = cards[current];
  const progress = cards.length > 0 ? ((current + 1) / cards.length) * 100 : 0;
  const masteredCount = cards.filter(c => c.isMastered).length;

  const handleNext = () => {
    if (current < cards.length - 1) { setCurrent(c => c + 1); setFlipped(false); }
    else setCompleted(true);
  };
  const handlePrev = () => { if (current > 0) { setCurrent(c => c - 1); setFlipped(false); } };

  const handleToggleMastered = async () => {
    if (!card) return;
    try {
      const { data } = await toggleMastered(card._id);
      setCards(prev => prev.map(c => c._id === card._id ? data.data : c));
      setSessionMastered(prev => {
        const next = new Set(prev);
        data.data.isMastered ? next.add(card._id) : next.delete(card._id);
        return next;
      });
    } catch { toast.error('Failed to update'); }
  };

  const handleReset = async () => {
    await resetDeckProgress(deckId);
    setCards(prev => prev.map(c => ({ ...c, isMastered: false })));
    setCurrent(0); setFlipped(false); setCompleted(false); setSessionMastered(new Set());
    toast.success('Progress reset!');
  };

  const handleRestart = () => {
    setCards(prev => [...prev].sort(() => Math.random() - 0.5));
    setCurrent(0); setFlipped(false); setCompleted(false); setSessionMastered(new Set());
  };

  if (loading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' }}>
      <div className="spinner" />
    </div>
  );

  if (completed) {
    const score = cards.length > 0 ? Math.round((masteredCount / cards.length) * 100) : 0;
    const emoji = score >= 80 ? '🏆' : score >= 60 ? '👏' : score >= 40 ? '💪' : '📚';
    const msg = score >= 80 ? 'Outstanding!' : score >= 60 ? 'Great job!' : score >= 40 ? 'Keep it up!' : 'Keep studying!';
    return (
      <div style={{ maxWidth: 520, margin: '0 auto', padding: '40px 24px', textAlign: 'center' }}>
        <div className="fade-in-up">
          <div style={{ fontSize: 80, marginBottom: 16 }}>{emoji}</div>
          <h1 style={{ fontSize: 32, fontWeight: 900, marginBottom: 6 }}>Session Complete!</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 17, marginBottom: 32 }}>{msg}</p>

          <div style={{ background: 'white', borderRadius: 'var(--radius-xl)', padding: 28, marginBottom: 24, boxShadow: 'var(--shadow-md)', border: '1px solid var(--border)' }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3,1fr)', gap: 16, marginBottom: 20 }}>
              {[
                { val: cards.length, label: 'Total Cards', color: 'var(--primary)' },
                { val: masteredCount, label: 'Mastered', color: 'var(--green)' },
                { val: `${score}%`, label: 'Score', color: 'var(--secondary)' },
              ].map(s => (
                <div key={s.label} style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: 28, fontWeight: 900, color: s.color }}>{s.val}</div>
                  <div style={{ fontSize: 13, color: 'var(--text-muted)', fontWeight: 600 }}>{s.label}</div>
                </div>
              ))}
            </div>
            {/* Score bar */}
            <div style={{ height: 12, background: 'var(--border)', borderRadius: 99, overflow: 'hidden' }}>
              <div style={{ height: '100%', width: `${score}%`, background: score >= 80 ? 'var(--green)' : score >= 60 ? 'var(--primary)' : 'var(--secondary)', borderRadius: 99, transition: 'width 1s ease' }} />
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <Button fullWidth size="lg" onClick={handleRestart}><RotateCcw size={16} /> Study Again</Button>
            <Button fullWidth variant="secondary" onClick={() => navigate(`/manage/${deckId}`)}> ✏️ Manage Cards</Button>
            <Button fullWidth variant="ghost" onClick={() => navigate('/')}><Home size={16} /> Back to Home</Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 680, margin: '0 auto', padding: '24px 24px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 24 }}>
        <button onClick={() => navigate('/')} style={{ display: 'flex', alignItems: 'center', gap: 6, background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', fontWeight: 700, fontSize: 14, fontFamily: 'var(--font-main)' }}>
          <ArrowLeft size={16} /> Home
        </button>
        <div style={{ textAlign: 'center' }}>
          <h1 style={{ fontSize: 18, fontWeight: 800, maxWidth: 280, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{deck?.title}</h1>
          <p style={{ fontSize: 13, color: 'var(--text-muted)' }}>{masteredCount} of {cards.length} mastered</p>
        </div>
        <button onClick={handleReset} style={{ display: 'flex', alignItems: 'center', gap: 4, background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', fontWeight: 600, fontSize: 13, fontFamily: 'var(--font-main)' }}>
          <RotateCcw size={14} /> Reset
        </button>
      </div>

      {/* Progress */}
      <div style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
          <span style={{ fontSize: 13, fontWeight: 700, color: 'var(--green)' }}>✓ {masteredCount} mastered</span>
          <span style={{ fontSize: 13, color: 'var(--text-muted)', fontWeight: 600 }}>{current + 1} / {cards.length}</span>
        </div>
        <div style={{ height: 8, background: 'var(--border)', borderRadius: 99, overflow: 'hidden' }}>
          <div style={{ height: '100%', width: `${progress}%`, background: 'linear-gradient(90deg, var(--primary), var(--primary-light))', borderRadius: 99, transition: 'width 0.4s ease' }} />
        </div>
      </div>

      {/* Flip Card */}
      {card && (
        <div style={{ marginBottom: 20 }}>
          <FlipCard
            question={card.question}
            answer={card.answer}
            isFlipped={flipped}
            isMastered={card.isMastered}
            onClick={() => setFlipped(f => !f)}
          />
        </div>
      )}

      {/* Show Answer */}
      {!flipped && (
        <Button fullWidth variant="secondary" size="lg" onClick={() => setFlipped(true)} style={{ marginBottom: 16 }}>
          <Eye size={16} /> Show Answer
        </Button>
      )}

      {/* Mastered button */}
      {flipped && card && (
        <Button fullWidth size="lg" variant={card.isMastered ? 'success' : 'secondary'} onClick={handleToggleMastered} style={{ marginBottom: 16 }}>
          <CheckCircle size={16} /> {card.isMastered ? '✓ Mastered!' : 'Mark as Mastered'}
        </Button>
      )}

      {/* Navigation */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button onClick={handlePrev} disabled={current === 0}
          style={{ width: 52, height: 52, borderRadius: '50%', border: 'none', cursor: current === 0 ? 'not-allowed' : 'pointer', background: current === 0 ? 'var(--border)' : 'var(--primary-bg)', color: current === 0 ? 'var(--text-light)' : 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all 0.2s' }}>
          <ChevronLeft size={24} />
        </button>

        <div style={{ textAlign: 'center' }}>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', fontWeight: 600 }}>
            {flipped ? 'Press F to flip · ← → to navigate' : 'Tap card or press F to flip'}
          </p>
        </div>

        <button onClick={handleNext}
          style={{ width: 52, height: 52, borderRadius: '50%', border: 'none', cursor: 'pointer', background: 'var(--gradient)', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 14px rgba(108,99,255,0.4)', transition: 'all 0.2s' }}>
          <ChevronRight size={24} />
        </button>
      </div>
    </div>
  );
}
