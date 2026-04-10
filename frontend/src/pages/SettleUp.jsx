import { useState } from 'react';
import { mockSettlements } from '../mockData';

export default function SettleUp() {
  const [settlements, setSettlements] = useState(mockSettlements);
  const [done, setDone] = useState([]);

  const markDone = (index) => {
    setDone([...done, index]);
  };

  return (
    <div style={{ maxWidth:600, margin:'40px auto', padding:24 }}>
      <h1>Settle Up</h1>
      <p>Minimum transactions to settle everything:</p>
      {settlements.map((s, i) => (
        <div key={i} style={{ border:'1px solid #ccc', padding:16, marginTop:12,
          opacity: done.includes(i) ? 0.4 : 1 }}>
          <strong>{s.from}</strong> pays <strong>{s.to}</strong> – Rs.{s.amount}
          {!done.includes(i) && (
            <button onClick={()=>markDone(i)}
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
