import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Zap, Home, Sparkles, Menu, X } from 'lucide-react';

export default function Navbar() {
  const location = useLocation();
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => setMobileOpen(false), [location]);

  const navStyle = {
    position: 'fixed', top: 0, left: 0, right: 0, zIndex: 1000,
    height: 72,
    background: scrolled ? 'rgba(248,244,255,0.92)' : 'transparent',
    backdropFilter: scrolled ? 'blur(16px)' : 'none',
    borderBottom: scrolled ? '1px solid var(--border)' : '1px solid transparent',
    transition: 'all 0.3s ease',
    display: 'flex', alignItems: 'center',
    padding: '0 24px',
  };

  return (
    <nav style={navStyle}>
      <div style={{ maxWidth: 1200, margin: '0 auto', width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        {/* Logo */}
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: 10, textDecoration: 'none' }}>
          <div style={{ width: 38, height: 38, borderRadius: 12, background: 'var(--gradient)', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 14px rgba(108,99,255,0.4)' }}>
            <Zap size={20} color="white" fill="white" />
          </div>
          <span style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 20, color: 'var(--text)' }}>
            Flash<span style={{ color: 'var(--primary)' }}>Card AI</span>
          </span>
        </Link>

        {/* Desktop Nav */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }} className="desktop-nav">
          <NavLink to="/" icon={<Home size={16} />} label="Home" active={location.pathname === '/'} />
          <NavLink to="/generate" icon={<Sparkles size={16} />} label="AI Generate" active={location.pathname === '/generate'} isPrimary />
        </div>

        {/* Mobile toggle */}
        <button
          onClick={() => setMobileOpen(o => !o)}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text)', display: 'none' }}
          className="mobile-toggle"
        >
          {mobileOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile menu */}
      {mobileOpen && (
        <div style={{
          position: 'absolute', top: 72, left: 0, right: 0,
          background: 'white', padding: 20,
          borderBottom: '1px solid var(--border)',
          display: 'flex', flexDirection: 'column', gap: 8,
          boxShadow: 'var(--shadow-md)',
        }}>
          <MobileNavLink to="/" label="🏠 Home" />
          <MobileNavLink to="/generate" label="✨ AI Generate" />
        </div>
      )}

      <style>{`
        @media (max-width: 600px) {
          .desktop-nav { display: none !important; }
          .mobile-toggle { display: block !important; }
        }
      `}</style>
    </nav>
  );
}

function NavLink({ to, icon, label, active, isPrimary }) {
  return (
    <Link to={to} style={{
      display: 'flex', alignItems: 'center', gap: 6,
      padding: '8px 16px', borderRadius: 10, textDecoration: 'none',
      fontWeight: 700, fontSize: 14, transition: 'all 0.2s',
      background: isPrimary ? 'var(--gradient)' : active ? 'var(--primary-bg)' : 'transparent',
      color: isPrimary ? 'white' : active ? 'var(--primary)' : 'var(--text-muted)',
      boxShadow: isPrimary ? '0 4px 14px rgba(108,99,255,0.35)' : 'none',
    }}>
      {icon}{label}
    </Link>
  );
}

function MobileNavLink({ to, label }) {
  return (
    <Link to={to} style={{ padding: '12px 16px', borderRadius: 12, textDecoration: 'none', fontWeight: 700, color: 'var(--text)', background: 'var(--surface)' }}>
      {label}
    </Link>
  );
}
