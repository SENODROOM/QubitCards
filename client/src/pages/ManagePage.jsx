import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft, Plus, Edit2, Trash2, CheckCircle, Play } from 'lucide-react';
import { getDeck, getCards, createCard, updateCard, deleteCard } from '../utils/api';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';

export default function ManagePage() {
  const { deckId } = useParams();
  const navigate = useNavigate();
  const [deck, setDeck] = useState(null);
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ question: '', answer: '' });
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [search, setSearch] = useState('');

  const load = useCallback(async () => {
    try {
      const [deckRes, cardsRes] = await Promise.all([getDeck(deckId), getCards(deckId)]);
      setDeck(deckRes.data.data);
      setCards(cardsRes.data.data);
    } catch { toast.error('Failed to load'); navigate('/'); }
    finally { setLoading(false); }
  }, [deckId, navigate]);

  useEffect(() => { load(); }, [load]);

  const openAdd = () => { setEditing(null); setForm({ question: '', answer: '' }); setShowModal(true); };
  const openEdit = (card) => { setEditing(card); setForm({ question: card.question, answer: card.answer }); setShowModal(true); };

  const handleSave = async () => {
    if (!form.question.trim() || !form.answer.trim()) return toast.error('Both fields are required');
    setSaving(true);
    try {
      if (editing) {
        const { data } = await updateCard(editing._id, form);
        setCards(prev => prev.map(c => c._id === editing._id ? data.data : c));
        toast.success('Card updated!');
      } else {
        const { data } = await createCard(deckId, form);
        setCards(prev => [...prev, data.data]);
        toast.success('Card added!');
      }
      setShowModal(false);
    } catch { toast.error('Failed to save card'); }
    finally { setSaving(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await deleteCard(deleteTarget._id);
      setCards(prev => prev.filter(c => c._id !== deleteTarget._id));
      toast.success('Card deleted');
    } catch { toast.error('Failed to delete'); }
    finally { setDeleteTarget(null); }
  };

  const filtered = cards.filter(c =>
    c.question.toLowerCase().includes(search.toLowerCase()) ||
    c.answer.toLowerCase().includes(search.toLowerCase())
  );

  const masteredCount = cards.filter(c => c.isMastered).length;

  if (loading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '60vh' }}>
      <div className="spinner" />
    </div>
  );

  return (
    <div style={{ maxWidth: 860, margin: '0 auto', padding: '32px 24px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 28 }}>
        <button onClick={() => navigate('/')} style={{ display: 'flex', alignItems: 'center', gap: 6, background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', fontWeight: 700, fontSize: 14, fontFamily: 'var(--font-main)' }}>
          <ArrowLeft size={16} /> Back
        </button>
        <div style={{ flex: 1 }}>
          <h1 style={{ fontSize: 24, fontWeight: 900, lineHeight: 1 }}>{deck?.title}</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 14, marginTop: 4 }}>
            {cards.length} cards · {masteredCount} mastered
          </p>
        </div>
        <Button size="sm" onClick={() => navigate(`/quiz/${deckId}`)} style={{ gap: 6 }}>
          <Play size={14} fill="white" /> Study
        </Button>
      </div>

      {/* Toolbar */}
      <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
        <input value={search} onChange={e => setSearch(e.target.value)} placeholder="🔍 Search cards..." style={{ ...inputStyle, flex: 1, minWidth: 200 }} />
        <Button onClick={openAdd}><Plus size={15} /> Add Card</Button>
      </div>

      {/* Stats bar */}
      {cards.length > 0 && (
        <div style={{ background: 'white', borderRadius: 'var(--radius-md)', padding: '14px 20px', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 16, border: '1px solid var(--border)' }}>
          <div style={{ flex: 1, height: 8, background: 'var(--border)', borderRadius: 99, overflow: 'hidden' }}>
            <div style={{ height: '100%', width: `${(masteredCount / cards.length) * 100}%`, background: 'linear-gradient(90deg, var(--green), #A5D6A7)', borderRadius: 99, transition: 'width 0.5s' }} />
          </div>
          <span style={{ fontSize: 13, fontWeight: 700, color: 'var(--green)', whiteSpace: 'nowrap' }}>
            {masteredCount}/{cards.length} mastered
          </span>
        </div>
      )}

      {/* Cards */}
      {filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px 20px' }}>
          <div style={{ fontSize: 56, marginBottom: 12 }}>{cards.length === 0 ? '🃏' : '🔍'}</div>
          <h3 style={{ fontSize: 20, fontWeight: 800, marginBottom: 8 }}>{cards.length === 0 ? 'No cards yet' : 'No results'}</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>{cards.length === 0 ? 'Add your first card to get started.' : 'Try a different search term.'}</p>
          {cards.length === 0 && <Button onClick={openAdd}><Plus size={14} /> Add First Card</Button>}
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {filtered.map((card, i) => (
            <div key={card._id}
              style={{ background: 'white', borderRadius: 'var(--radius-md)', padding: 18, border: `1px solid ${card.isMastered ? 'rgba(102,187,106,0.3)' : 'var(--border)'}`, display: 'flex', gap: 14, alignItems: 'flex-start', transition: 'all 0.2s', animation: `fadeInUp 0.3s ease ${i * 0.02}s forwards`, opacity: 0 }}
              onMouseEnter={e => { e.currentTarget.style.boxShadow = 'var(--shadow-sm)'; e.currentTarget.style.transform = 'translateX(2px)'; }}
              onMouseLeave={e => { e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.transform = ''; }}
            >
              {/* Index / mastered */}
              <div style={{ width: 32, height: 32, borderRadius: 10, background: card.isMastered ? 'var(--green-bg)' : 'var(--primary-bg)', color: card.isMastered ? 'var(--green)' : 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 13, fontWeight: 800, flexShrink: 0 }}>
                {card.isMastered ? <CheckCircle size={16} /> : i + 1}
              </div>

              <div style={{ flex: 1, minWidth: 0 }}>
                <p style={{ fontWeight: 700, marginBottom: 8, fontSize: 15, color: 'var(--text)' }}>{card.question}</p>
                <div style={{ borderTop: '1px dashed var(--border)', paddingTop: 8 }}>
                  <p style={{ color: 'var(--text-muted)', fontSize: 14, lineHeight: 1.5 }}>{card.answer}</p>
                </div>
              </div>

              <div style={{ display: 'flex', gap: 4, flexShrink: 0 }}>
                <button onClick={() => openEdit(card)} style={iconBtnStyle('#6C63FF')}>
                  <Edit2 size={14} />
                </button>
                <button onClick={() => setDeleteTarget(card)} style={iconBtnStyle('#E53935', '#FFF0F0')}>
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add/Edit Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title={editing ? 'Edit Card' : 'New Flashcard'}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div>
            <label style={labelStyle}>Question *</label>
            <textarea value={form.question} onChange={e => setForm(f => ({ ...f, question: e.target.value }))} placeholder="Enter your question..." rows={3} style={{ ...inputStyle, resize: 'vertical' }} />
          </div>
          <div>
            <label style={labelStyle}>Answer *</label>
            <textarea value={form.answer} onChange={e => setForm(f => ({ ...f, answer: e.target.value }))} placeholder="Enter the answer..." rows={3} style={{ ...inputStyle, resize: 'vertical' }} />
          </div>
          <div style={{ display: 'flex', gap: 10, marginTop: 4 }}>
            <Button variant="ghost" fullWidth onClick={() => setShowModal(false)}>Cancel</Button>
            <Button fullWidth loading={saving} onClick={handleSave}>{editing ? 'Save Changes' : 'Add Card'}</Button>
          </div>
        </div>
      </Modal>

      {/* Delete confirm */}
      <Modal isOpen={!!deleteTarget} onClose={() => setDeleteTarget(null)} title="Delete Card?" maxWidth={420}>
        <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>This card will be permanently deleted.</p>
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
  background: 'var(--surface)', transition: 'border-color 0.2s', boxSizing: 'border-box',
};
const labelStyle = { display: 'block', fontSize: 13, fontWeight: 800, marginBottom: 6, color: 'var(--primary)' };
const iconBtnStyle = (color, bg = 'var(--primary-bg)') => ({
  width: 32, height: 32, borderRadius: 8, border: 'none', cursor: 'pointer',
  background: 'transparent', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all 0.15s',
  ':hover': { background: bg, color }
});
