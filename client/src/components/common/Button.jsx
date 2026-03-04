import React from 'react';

const variants = {
  primary: { background: 'var(--gradient)', color: 'white', border: 'none', boxShadow: '0 4px 18px rgba(108,99,255,0.35)' },
  secondary: { background: 'var(--primary-bg)', color: 'var(--primary)', border: 'none' },
  outline: { background: 'transparent', color: 'var(--primary)', border: '2px solid var(--primary)' },
  danger: { background: '#FFE4E4', color: '#E53935', border: 'none' },
  ghost: { background: 'transparent', color: 'var(--text-muted)', border: 'none' },
  success: { background: 'var(--green-bg)', color: 'var(--green)', border: 'none' },
};

export default function Button({ children, variant = 'primary', size = 'md', disabled, loading, onClick, fullWidth, style = {}, ...props }) {
  const sizes = { sm: { padding: '7px 16px', fontSize: 13 }, md: { padding: '11px 22px', fontSize: 15 }, lg: { padding: '14px 30px', fontSize: 16 } };

  return (
    <button
      onClick={onClick}
      disabled={disabled || loading}
      style={{
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        borderRadius: 12, fontFamily: 'var(--font-main)', fontWeight: 700,
        cursor: disabled || loading ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        transition: 'all 0.2s ease',
        width: fullWidth ? '100%' : 'auto',
        whiteSpace: 'nowrap',
        ...sizes[size],
        ...variants[variant],
        ...style,
      }}
      onMouseEnter={e => { if (!disabled && !loading) e.currentTarget.style.transform = 'translateY(-1px)'; e.currentTarget.style.filter = 'brightness(1.05)'; }}
      onMouseLeave={e => { e.currentTarget.style.transform = ''; e.currentTarget.style.filter = ''; }}
      {...props}
    >
      {loading ? <span className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> : children}
    </button>
  );
}
