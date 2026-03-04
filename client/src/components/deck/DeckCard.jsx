import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { MoreVertical, Play, Edit2, Trash2, Bot, BookOpen, RotateCcw } from 'lucide-react';

const DECK_COLORS = [
  { from: '#6C63FF', to: '#9C6FFF' },
  { from: '#FF6584', to: '#FF8FAB' },
  { from: '#26C6DA', to: '#80DEEA' },
  { from: '#26A69A', to: '#80CBC4' },
  { from: '#FFA726', to: '#FFCA28' },
  { from: '#EF5350', to: '#FF8A80' },
];

export default function DeckCard({ deck, onDelete, onEdit }) {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const color = DECK_COLORS[parseInt(deck._id?.slice(-2), 16) % DECK_COLORS.length] || DECK_COLORS[0];
  const progress = deck.cardCount > 0 ? (deck.masteredCount / deck.cardCount) * 100 : 0;

  return (
    <div style={{
      background: 'white', borderRadius: 'var(--radius-lg)',
      boxShadow: 'var(--shadow-sm)',
      overflow: 'hidden', transition: 'all 0.25s ease',
      border: '1px solid var(--border)',
      animation: 'fadeInUp 0.4s ease forwards',
    }}
    onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-4px)'; e.currentTarget.style.boxShadow = 'var(--shadow-md)'; }}
    onMouseLeave={e => { e.currentTarget.style.transform = ''; e.currentTarget.style.boxShadow = 'var(--shadow-sm)'; }}
    >
      {/* Color banner */}
      <div style={{ height: 6, background: `linear-gradient(90deg, ${color.from}, ${color.to})` }} />

      <div style={{ padding: 20 }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14, marginBottom: 16 }}>
          {/* Icon */}
          <div style={{ width: 48, height: 48, borderRadius: 14, background: `linear-gradient(135deg, ${color.from}, ${color.to})`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, fontSize: 22 }}>
            {deck.isAiGenerated ? '🤖' : '📖'}
          </div>

          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <h3 style={{ fontSize: 16, fontWeight: 800, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', color: 'var(--text)' }}>{deck.title}</h3>
              {deck.isAiGenerated && (
                <span style={{ background: 'var(--primary-bg)', color: 'var(--primary)', fontSize: 11, fontWeight: 700, padding: '2px 8px', borderRadius: 20, flexShrink: 0, display: 'flex', alignItems: 'center', gap: 3 }}>
                  <Bot size={10} /> AI
                </span>
              )}
            </div>
            <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 2 }}>
              {deck.cardCount} cards · {deck.masteredCount} mastered
            </p>
          </div>

          {/* Menu */}
          <div style={{ position: 'relative', flexShrink: 0 }}>
            <button onClick={() => setMenuOpen(o => !o)} style={{ background: 'var(--surface)', border: 'none', borderRadius: 8, width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', color: 'var(--text-muted)' }}>
              <MoreVertical size={16} />
            </button>
            {menuOpen && (
              <>
                <div onClick={() => setMenuOpen(false)} style={{ position: 'fixed', inset: 0, zIndex: 10 }} />
                <div style={{ position: 'absolute', right: 0, top: 36, background: 'white', borderRadius: 12, boxShadow: 'var(--shadow-md)', border: '1px solid var(--border)', zIndex: 20, width: 160, overflow: 'hidden' }}>
                  {[
                    { icon: <Play size={14} />, label: 'Study', action: () => { navigate(`/quiz/${deck._id}`); setMenuOpen(false); } },
                    { icon: <Edit2 size={14} />, label: 'Manage Cards', action: () => { navigate(`/manage/${deck._id}`); setMenuOpen(false); } },
                    { icon: <RotateCcw size={14} />, label: 'Edit Deck', action: () => { onEdit(deck); setMenuOpen(false); } },
                    { icon: <Trash2 size={14} />, label: 'Delete', action: () => { onDelete(deck); setMenuOpen(false); }, danger: true },
                  ].map(item => (
                    <button key={item.label} onClick={item.action} style={{ width: '100%', display: 'flex', alignItems: 'center', gap: 10, padding: '10px 14px', background: 'none', border: 'none', cursor: 'pointer', fontSize: 14, fontWeight: 600, color: item.danger ? '#E53935' : 'var(--text)', fontFamily: 'var(--font-main)', transition: 'background 0.15s' }}
                      onMouseEnter={e => e.currentTarget.style.background = item.danger ? '#FFF0F0' : 'var(--surface)'}
                      onMouseLeave={e => e.currentTarget.style.background = 'none'}
                    >
                      {item.icon}{item.label}
                    </button>
                  ))}
                </div>
              </>
            )}
          </div>
        </div>

        {/* Progress bar */}
        <div style={{ marginBottom: 16 }}>
          <div style={{ height: 6, background: 'var(--border)', borderRadius: 99, overflow: 'hidden' }}>
            <div style={{ height: '100%', width: `${progress}%`, background: `linear-gradient(90deg, ${color.from}, ${color.to})`, borderRadius: 99, transition: 'width 0.6s ease' }} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6 }}>
            <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 600 }}>{Math.round(progress)}% mastered</span>
            <span style={{ fontSize: 12, color: 'var(--text-light)' }}>{deck.cardCount} total</span>
          </div>
        </div>

        {/* Actions */}
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={() => navigate(`/manage/${deck._id}`)} style={{ flex: 1, padding: '9px', borderRadius: 10, border: '2px solid var(--border)', background: 'transparent', cursor: 'pointer', fontSize: 13, fontWeight: 700, color: 'var(--text-muted)', fontFamily: 'var(--font-main)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5, transition: 'all 0.2s' }}
            onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--primary)'; e.currentTarget.style.color = 'var(--primary)'; }}
            onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.color = 'var(--text-muted)'; }}
          >
            <Edit2 size={13} /> Edit
          </button>
          <button onClick={() => navigate(`/quiz/${deck._id}`)} style={{ flex: 2, padding: '9px', borderRadius: 10, border: 'none', background: `linear-gradient(135deg, ${color.from}, ${color.to})`, cursor: 'pointer', fontSize: 13, fontWeight: 800, color: 'white', fontFamily: 'var(--font-main)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5, boxShadow: '0 3px 12px rgba(108,99,255,0.3)', transition: 'all 0.2s' }}
            onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-1px)'; }}
            onMouseLeave={e => { e.currentTarget.style.transform = ''; }}
          >
            <Play size={13} fill="white" /> Study Now
          </button>
        </div>
      </div>
    </div>
  );
}
