import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { mockExpenses, mockBalances } from '../mockData';

export default function GroupDetail() {
  const [expenses, setExpenses] = useState(mockExpenses);
  const [balances, setBalances] = useState(mockBalances);
  const [inviteLink, setInviteLink] = useState('');
  const navigate = useNavigate();

  const getInviteLink = () => {
    setInviteLink('http://localhost:8080/api/invite/join/mock-uuid-123');
  };

  return (
    <div style={{ maxWidth:700, margin:'40px auto', padding:24 }}>
      <h1>Kodai Trip</h1>
      <button onClick={getInviteLink}>Share Invite Link</button>
      {inviteLink && <p>Link: <a href={inviteLink}>{inviteLink}</a></p>}

      <h2>Expenses</h2>
      <button onClick={()=>{}}>+ Add Expense</button>
      {expenses.map(e => (
        <div key={e.id} style={{ border:'1px solid #eee', padding:12, marginTop:8 }}>
          <strong>{e.description}</strong> – Rs.{e.amount} – paid by {e.paidBy}
          <span style={{ float:'right', background:'#eee', padding:'2px 8px', borderRadius:4 }}>{e.category}</span>
        </div>
      ))}

      <h2>Balances</h2>
      {balances.map(b => (
        <div key={b.userId} style={{ padding:8, marginTop:4,
          color: b.netBalance > 0 ? 'green' : b.netBalance < 0 ? 'red' : 'gray' }}>
          {b.name}: {b.netBalance > 0 ? `gets back Rs.${b.netBalance}` : b.netBalance < 0 ? `owes Rs.${Math.abs(b.netBalance)}` : 'all settled'}
        </div>
      ))}

      <button onClick={()=>navigate(`/groups/1/settle`)}
        style={{ marginTop:16, padding:'10px 20px' }}>
        Settle Up
      </button>
    </div>
  );
}
