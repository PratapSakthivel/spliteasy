import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axios';

export default function SettleUp() {
  const { id } = useParams();
  const [settlements, setSettlements] = useState([]);
  const [done, setDone] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/api/groups/${id}/settle`)
      .then(res => {
        setSettlements(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to load settlements', err);
        setLoading(false);
      });
  }, [id]);

  const markDone = async (settlement, index) => {
    try {
      await api.post('/api/settlements', {
        groupId: id,
        paidById: settlement.fromId,
        paidToId: settlement.toId,
        amount: settlement.amount
      });
      setDone([...done, index]);
    } catch (err) {
      console.error('Failed to mark settlement', err);
    }
  };

  if (loading) return <div style={{ textAlign:'center', marginTop:100 }}>Loading...</div>;

  return (
    <div style={{ maxWidth:600, margin:'40px auto', padding:24 }}>
      <h1>Settle Up</h1>
      <p>Minimum transactions to settle everything:</p>
      {settlements.length === 0 && <p>All settled! No transactions needed.</p>}
      {settlements.map((s, i) => (
        <div key={i} style={{ border:'1px solid #ccc', padding:16, marginTop:12,
          opacity: done.includes(i) ? 0.4 : 1 }}>
          <strong>{s.from || s.fromName}</strong> pays <strong>{s.to || s.toName}</strong> – Rs.{s.amount}
          {!done.includes(i) && (
            <button onClick={()=>markDone(s, i)}
              style={{ float:'right', padding:'4px 12px', background:'green', color:'white', border:'none', cursor:'pointer' }}>
              Mark as Paid
            </button>
          )}
          {done.includes(i) && <span style={{ float:'right', color:'green' }}>✓ Done</span>}
        </div>
      ))}
    </div>
  );
}
