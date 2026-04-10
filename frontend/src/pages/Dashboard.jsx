import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { mockGroups } from '../mockData';

export default function Dashboard() {
  const [groups, setGroups] = useState(mockGroups);
  const [showForm, setShowForm] = useState(false);
  const [newName, setNewName] = useState('');
  const navigate = useNavigate();

  const createGroup = () => {
    setGroups([...groups, { id: Date.now(), name: newName, memberCount: 1 }]);
    setShowForm(false); setNewName('');
  };

  return (
    <div style={{ maxWidth:600, margin:'40px auto', padding:24 }}>
      <h1>My Groups</h1>
      <button onClick={()=>setShowForm(true)}>+ Create Group</button>
      {showForm && (
        <div>
          <input placeholder='Group name' value={newName} onChange={e=>setNewName(e.target.value)} />
          <button onClick={createGroup}>Create</button>
        </div>
      )}
      {groups.map(g => (
        <div key={g.id} onClick={()=>navigate(`/groups/${g.id}`)}
          style={{ border:'1px solid #ccc', padding:16, marginTop:12, cursor:'pointer' }}>
          <h3>{g.name}</h3>
          <p>{g.memberCount} members</p>
        </div>
      ))}
    </div>
  );
}
