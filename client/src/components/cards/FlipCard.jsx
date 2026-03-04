import React from 'react';

export default function FlipCard({ question, answer, isFlipped, isMastered, onClick, style = {} }) {
  return (
    <div
      className="flip-container"
      onClick={onClick}
      style={{ width: '100%', height: 280, cursor: 'pointer', userSelect: 'none', ...style }}
    >
      <div className={`flip-inner${isFlipped ? ' flipped' : ''}`} style={{ width: '100%', height: '100%' }}>
        {/* Front */}
        <div className="flip-front" style={{
          background: 'white',
          boxShadow: '0 8px 40px rgba(108,99,255,0.15)',
          border: '2px solid var(--border)',
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          padding: 36, textAlign: 'center', position: 'relative', overflow: 'hidden',
        }}>
          {/* Decorative circles */}
          <div style={{ position: 'absolute', top: -30, right: -30, width: 120, height: 120, borderRadius: '50%', background: 'var(--primary-bg)', opacity: 0.5 }} />
          <div style={{ position: 'absolute', bottom: -20, left: -20, width: 80, height: 80, borderRadius: '50%', background: 'var(--secondary-bg)', opacity: 0.4 }} />

          {isMastered && (
            <div style={{ position: 'absolute', top: 16, right: 16, background: 'var(--green-bg)', color: 'var(--green)', fontSize: 12, fontWeight: 800, padding: '4px 12px', borderRadius: 20, display: 'flex', alignItems: 'center', gap: 4 }}>
              ✓ Mastered
            </div>
          )}

          <span style={{ fontSize: 11, fontWeight: 800, letterSpacing: 3, color: 'var(--primary)', opacity: 0.7, marginBottom: 16 }}>QUESTION</span>
          <p style={{ fontSize: 19, fontWeight: 700, color: 'var(--text)', lineHeight: 1.5, zIndex: 1 }}>{question}</p>
          <span style={{ position: 'absolute', bottom: 16, fontSize: 12, color: 'var(--text-light)', fontWeight: 600 }}>Tap to reveal answer</span>
        </div>

        {/* Back */}
        <div className="flip-back" style={{
          background: 'linear-gradient(135deg, var(--primary), var(--primary-light))',
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          padding: 36, textAlign: 'center', position: 'relative', overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', top: -30, left: -30, width: 130, height: 130, borderRadius: '50%', background: 'rgba(255,255,255,0.08)' }} />
          <div style={{ position: 'absolute', bottom: -20, right: -20, width: 90, height: 90, borderRadius: '50%', background: 'rgba(255,255,255,0.05)' }} />

          <span style={{ fontSize: 11, fontWeight: 800, letterSpacing: 3, color: 'rgba(255,255,255,0.6)', marginBottom: 16 }}>ANSWER</span>
          <p style={{ fontSize: 18, fontWeight: 600, color: 'white', lineHeight: 1.6, zIndex: 1 }}>{answer}</p>
        </div>
      </div>
    </div>
  );
}
