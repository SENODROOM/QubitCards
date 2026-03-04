import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Sparkles, Plus, BookOpen, Award, Layers, TrendingUp, X } from 'lucide-react';
import { getDecks, createDeck, updateDeck, deleteDeck } from '../utils/api';
import DeckCard from '../components/deck/DeckCard';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';

export default function HomePage() {
  const navigate = useNavigate();
  const [decks, setDecks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [editTarget, setEditTarget] = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const [form, setForm] = useState({ title: '', description: '' });
  const [saving, setSaving] = useState(false);

  const fetchDecks = useCallback(async () => {
    try {
      const { data } = await getDecks();
      setDecks(data.data || []);
    } catch {
      toast.error('Failed to load decks');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchDecks(); }, [fetchDecks]);

  const totalCards = decks.reduce((s, d) => s + (d.cardCount || 0), 0);
  const totalMastered = decks.reduce((s, d) => s + (d.masteredCount || 0), 0);
  const overallProgress = totalCards > 0 ? Math.round((totalMastered / totalCards) * 100) : 0;

  const handleSaveDeck = async () => {
    if (!form.title.trim()) return toast.error('Title is required');
    setSaving(true);
    try {
      if (editTarget) {
        const { data } = await updateDeck(editTarget._id, form);
        setDecks(prev => prev.map(d => d._id === editTarget._id ? { ...d, ...data.data } : d));
        toast.success('Deck updated!');
      } else {
        const { data } = await createDeck({ ...form, isAiGenerated: false });
        setDecks(prev => [data.data, ...prev]);
        toast.success('Deck created!');
      }
      setShowCreate(false); setEditTarget(null); setForm({ title: '', description: '' });
    } catch { toast.error('Failed to save deck'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteDeck(deleteTarget._id);
      setDecks(prev => prev.filter(d => d._id !== deleteTarget._id));
      toast.success('Deck deleted');
    } catch { toast.error('Failed to delete'); }
    finally { setDeleteTarget(null); }
  };

  const openEdit = (deck) => { setEditTarget(deck); setForm({ title: deck.title, description: deck.description || '' }); setShowCreate(true); };

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto', padding: '32px 24px' }}>

      {/* Hero */}
      <div style={{ borderRadius: 'var(--radius-xl)', background: 'linear-gradient(135deg, #6C63FF 0%, #9C6FFF 60%, #FF6584 100%)', padding: '48px 40px', marginBottom: 32, position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: -60, right: -60, width: 240, height: 240, borderRadius: '50%', background: 'rgba(255,255,255,0.08)' }} />
        <div style={{ position: 'absolute', bottom: -40, left: '30%', width: 160, height: 160, borderRadius: '50%', background: 'rgba(255,255,255,0.05)' }} />
        <div style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16, justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h1 style={{ fontSize: 'clamp(28px,5vw,42px)', fontWeight: 900, color: 'white', marginBottom: 10 }}>
                ⚡ FlashCard AI
              </h1>
              <p style={{ color: 'rgba(255,255,255,0.85)', fontSize: 17, maxWidth: 440, lineHeight: 1.6 }}>
                Study smarter with AI-powered flashcards. Generate a complete deck on any topic in seconds.
              </p>
              <div style={{ display: 'flex', gap: 12, marginTop: 20, flexWrap: 'wrap' }}>
                <Button onClick={() => navigate('/generate')} style={{ background: 'white', color: 'var(--primary)', boxShadow: '0 4px 20px rgba(0,0,0,0.2)' }}>
                  <Sparkles size={16} /> AI Generate
                </Button>
                <Button onClick={() => { setEditTarget(null); setForm({ title: '', description: '' }); setShowCreate(true); }} style={{ background: 'rgba(255,255,255,0.15)', color: 'white', boxShadow: 'none', border: '1px solid rgba(255,255,255,0.3)' }}>
                  <Plus size={16} /> New Deck
                </Button>
              </div>
            </div>
            {/* Stats */}
            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              {[
                { icon: <Layers size={20} />, val: decks.length, label: 'Decks' },
                { icon: <BookOpen size={20} />, val: totalCards, label: 'Cards' },
                { icon: <Award size={20} />, val: totalMastered, label: 'Mastered' },
                { icon: <TrendingUp size={20} />, val: `${overallProgress}%`, label: 'Progress' },
              ].map(s => (
                <div key={s.label} style={{ background: 'rgba(255,255,255,0.15)', backdropFilter: 'blur(10px)', borderRadius: 16, padding: '16px 20px', textAlign: 'center', minWidth: 80, border: '1px solid rgba(255,255,255,0.2)' }}>
                  <div style={{ color: 'rgba(255,255,255,0.8)', marginBottom: 4 }}>{s.icon}</div>
                  <div style={{ fontSize: 22, fontWeight: 900, color: 'white' }}>{s.val}</div>
                  <div style={{ fontSize: 12, color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>{s.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Deck Grid */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: 22, fontWeight: 800 }}>My Decks</h2>
        <Button variant="secondary" size="sm" onClick={() => { setEditTarget(null); setForm({ title: '', description: '' }); setShowCreate(true); }}>
          <Plus size={14} /> Add Deck
        </Button>
      </div>

      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 20 }}>
          {[1, 2, 3].map(i => <div key={i} className="skeleton" style={{ height: 220, borderRadius: 'var(--radius-lg)' }} />)}
        </div>
      ) : decks.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '80px 20px' }}>
          <div style={{ fontSize: 72, marginBottom: 16 }}>📚</div>
          <h3 style={{ fontSize: 24, fontWeight: 800, marginBottom: 8 }}>No decks yet!</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: 24 }}>Create your first deck manually or let AI generate one for you.</p>
          <Button onClick={() => navigate('/generate')}>
            <Sparkles size={16} /> Generate with AI
          </Button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 20 }}>
          {decks.map(deck => (
            <DeckCard key={deck._id} deck={deck} onDelete={setDeleteTarget} onEdit={openEdit} />
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      <Modal isOpen={showCreate} onClose={() => { setShowCreate(false); setEditTarget(null); }} title={editTarget ? 'Edit Deck' : 'New Deck'}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div>
            <label style={{ display: 'block', fontSize: 13, fontWeight: 700, marginBottom: 6, color: 'var(--primary)' }}>Deck Title *</label>
            <input value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} placeholder="e.g. Biology 101" style={inputStyle} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: 13, fontWeight: 700, marginBottom: 6, color: 'var(--primary)' }}>Description</label>
            <textarea value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} placeholder="Optional description..." rows={3} style={{ ...inputStyle, resize: 'vertical' }} />
          </div>
          <div style={{ display: 'flex', gap: 10, marginTop: 4 }}>
            <Button variant="ghost" fullWidth onClick={() => { setShowCreate(false); setEditTarget(null); }}>Cancel</Button>
            <Button fullWidth loading={saving} onClick={handleSaveDeck}>{editTarget ? 'Save Changes' : 'Create Deck'}</Button>
          </div>
        </div>
      </Modal>

      {/* Delete confirm */}
      <Modal isOpen={!!deleteTarget} onClose={() => setDeleteTarget(null)} title="Delete Deck?" maxWidth={420}>
        <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>
          <strong>"{deleteTarget?.title}"</strong> and all its cards will be permanently deleted. This cannot be undone.
        </p>
        <div style={{ display: 'flex', gap: 10 }}>
          <Button variant="ghost" fullWidth onClick={() => setDeleteTarget(null)}>Cancel</Button>
          <Button variant="danger" fullWidth onClick={handleDelete}>Delete</Button>
        </div>
      </Modal>
    </div>
  );
}

const inputStyle = {
  width: '100%', padding: '11px 14px', borderRadius: 12,
  border: '2px solid var(--border)', outline: 'none',
  fontSize: 15, fontFamily: 'var(--font-main)', fontWeight: 500,
  background: 'var(--surface)', transition: 'border-color 0.2s',
  boxSizing: 'border-box',
};
