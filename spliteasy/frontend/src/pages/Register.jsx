import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function Register() {
  const [name, setName]         = useState('');
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleRegister = async () => {
    try {
      const res = await api.post('/api/auth/register', { name, email, password });
      login({ name: res.data.name, email: res.data.email }, res.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError('Registration failed. Email might already exist.');
      console.error(err);
    }
  };

  return (
    <div style={{ maxWidth:400, margin:'100px auto', padding:24 }}>
      <h1>SplitEasy Register</h1>
      {error && <p style={{ color:'red' }}>{error}</p>}
      <input placeholder='Name' value={name}
        onChange={e=>setName(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }} />
      <input placeholder='Email' value={email}
        onChange={e=>setEmail(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }} />
      <input placeholder='Password' type='password' value={password}
        onChange={e=>setPassword(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:16, padding:8 }} />
      <button onClick={handleRegister}
        style={{ width:'100%', padding:10, background:'#1E3A5F', color:'white', border:'none', cursor:'pointer' }}>
        Register
      </button>
      <p>Have an account? <a href='/login'>Login</a></p>
    </div>
  );
}
