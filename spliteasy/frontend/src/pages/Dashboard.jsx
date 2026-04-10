import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function Dashboard() {
  const [groups, setGroups] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newName, setNewName] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/api/groups')
      .then(res => {
        setGroups(res.data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to load groups', err);
        setLoading(false);
      });
  }, []);

  const createGroup = async () => {
    try {
      const res = await api.post('/api/groups', { name: newName });
      setGroups([...groups, res.data]);
      setShowForm(false);
      setNewName('');
    } catch (err) {
      console.error('Failed to create group', err);
    }
  };

  if (loading) return <div style={{ textAlign:'center', marginTop:100 }}>Loading...</div>;

  return (
    <div style={{ maxWidth:600, margin:'40px auto', padding:24 }}>
      <h1>My Groups</h1>
      <button onClick={()=>setShowForm(true)}>+ Create Group</button>
      {showForm && (
        <div style={{ marginTop:16 }}>
          <input placeholder='Group name' value={newName} onChange={e=>setNewName(e.target.value)}
            style={{ padding:8, marginRight:8 }} />
          <button onClick={createGroup}>Create</button>
          <button onClick={()=>setShowForm(false)} style={{ marginLeft:8 }}>Cancel</button>
        </div>
      )}
      {groups.length === 0 && <p>No groups yet. Create one!</p>}
      {groups.map(g => (
        <div key={g.id} onClick={()=>navigate(`/groups/${g.id}`)}
          style={{ border:'1px solid #ccc', padding:16, marginTop:12, cursor:'pointer' }}>
          <h3>{g.name}</h3>
          <p>{g.memberCount || g.members?.length || 0} members</p>
        </div>
      ))}
    </div>
  );
}
