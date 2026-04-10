import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';

const CATEGORIES = ['Food','Hotel','Travel','Fuel','Entertainment','Other'];

export default function AddExpenseForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [description, setDescription] = useState('');
  const [amount, setAmount]           = useState('');
  const [category, setCategory]       = useState('Food');
  const [paidById, setPaidById]       = useState('');
  const [splitAmong, setSplitAmong]   = useState([]);
  const [members, setMembers]         = useState([]);
  const [loading, setLoading]         = useState(true);

  useEffect(() => {
    api.get(`/api/groups/${id}`)
      .then(res => {
        const groupMembers = res.data.members || [];
        setMembers(groupMembers);
        if (groupMembers.length > 0) {
          setPaidById(groupMembers[0].id);
          setSplitAmong(groupMembers.map(m => m.id));
        }
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to load members', err);
        setLoading(false);
      });
  }, [id]);

  const toggleMember = (memberId) => {
    setSplitAmong(prev => 
      prev.includes(memberId) ? prev.filter(x => x !== memberId) : [...prev, memberId]
    );
  };

  const handleSubmit = async () => {
    try {
      await api.post(`/api/groups/${id}/expenses`, {
        description,
        amount: parseFloat(amount),
        category,
        paidById,
        splitAmong
      });
      navigate(`/groups/${id}`);
    } catch (err) {
      console.error('Failed to add expense', err);
      alert('Failed to add expense. Please try again.');
    }
  };

  if (loading) return <div style={{ textAlign:'center', marginTop:100 }}>Loading...</div>;

  return (
    <div style={{ maxWidth:600, margin:'40px auto', padding:24 }}>
      <h2>Add Expense</h2>
      <input placeholder='Description (e.g. Hotel)'
        value={description} onChange={e=>setDescription(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }} />
      <input placeholder='Amount (e.g. 900)'
        type='number' value={amount} onChange={e=>setAmount(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }} />
      <select value={category} onChange={e=>setCategory(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }}>
        {CATEGORIES.map(c => <option key={c}>{c}</option>)}
      </select>
      <p>Who paid?</p>
      <select value={paidById} onChange={e=>setPaidById(Number(e.target.value))}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }}>
        {members.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
      </select>
      <p>Split among (select all that apply):</p>
      {members.map(m => (
        <label key={m.id} style={{ display:'block', marginBottom:4 }}>
          <input type='checkbox'
            checked={splitAmong.includes(m.id)}
            onChange={()=>toggleMember(m.id)} />
          {' '}{m.name}
        </label>
      ))}
      <button onClick={handleSubmit} style={{ marginTop:12, padding:'10px 20px' }}>
        Save Expense
      </button>
      <button onClick={()=>navigate(`/groups/${id}`)} style={{ marginLeft:8, padding:'10px 20px' }}>
        Cancel
      </button>
    </div>
  );
}
