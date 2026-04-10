import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axios';

export default function GroupDetail() {
  const { id } = useParams();
  const [group, setGroup] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [balances, setBalances] = useState([]);
  const [inviteLink, setInviteLink] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      api.get(`/api/groups/${id}`),
      api.get(`/api/groups/${id}/expenses`),
      api.get(`/api/groups/${id}/balances`)
    ])
      .then(([groupRes, expensesRes, balancesRes]) => {
        setGroup(groupRes.data);
        setExpenses(expensesRes.data);
        setBalances(balancesRes.data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to load group details', err);
        setLoading(false);
      });
  }, [id]);

  const getInviteLink = async () => {
    try {
      const res = await api.get(`/api/groups/${id}/invite-link`);
      setInviteLink(res.data.inviteLink);
    } catch (err) {
      console.error('Failed to get invite link', err);
    }
  };

  if (loading) return <div style={{ textAlign:'center', marginTop:100 }}>Loading...</div>;
  if (!group) return <div style={{ textAlign:'center', marginTop:100 }}>Group not found</div>;

  return (
    <div style={{ maxWidth:700, margin:'40px auto', padding:24 }}>
      <h1>{group.name}</h1>
      <button onClick={getInviteLink}>Share Invite Link</button>
      {inviteLink && <p>Link: <a href={inviteLink}>{inviteLink}</a></p>}

      <h2>Expenses</h2>
      <button onClick={()=>navigate(`/groups/${id}/add-expense`)}>+ Add Expense</button>
      {expenses.length === 0 && <p>No expenses yet.</p>}
      {expenses.map(e => (
        <div key={e.id} style={{ border:'1px solid #eee', padding:12, marginTop:8 }}>
          <strong>{e.description}</strong> – Rs.{e.amount} – paid by {e.paidBy || e.paidByName}
          <span style={{ float:'right', background:'#eee', padding:'2px 8px', borderRadius:4 }}>{e.category}</span>
        </div>
      ))}

      <h2>Balances</h2>
      {balances.length === 0 && <p>All settled!</p>}
      {balances.map(b => (
        <div key={b.userId || b.id} style={{ padding:8, marginTop:4,
          color: b.netBalance > 0 ? 'green' : b.netBalance < 0 ? 'red' : 'gray' }}>
          {b.name}: {b.netBalance > 0 ? `gets back Rs.${b.netBalance}` : b.netBalance < 0 ? `owes Rs.${Math.abs(b.netBalance)}` : 'all settled'}
        </div>
      ))}

      <button onClick={()=>navigate(`/groups/${id}/settle`)}
        style={{ marginTop:16, padding:'10px 20px' }}>
        Settle Up
      </button>
    </div>
  );
}
