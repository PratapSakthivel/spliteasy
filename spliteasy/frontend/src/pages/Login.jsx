import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

export default function Login() {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async () => {
    try {
      const res = await api.post('/api/auth/login', { email, password });
      login({ name: res.data.name, email: res.data.email }, res.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError('Login failed. Check your email and password.');
      console.error(err);
    }
  };

  return (
    <div style={{ maxWidth:400, margin:'100px auto', padding:24 }}>
      <h1>SplitEasy Login</h1>
      {error && <p style={{ color:'red' }}>{error}</p>}
      <input placeholder='Email'    value={email}
        onChange={e=>setEmail(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:12, padding:8 }} />
      <input placeholder='Password' type='password' value={password}
        onChange={e=>setPassword(e.target.value)}
        style={{ display:'block', width:'100%', marginBottom:16, padding:8 }} />
      <button onClick={handleLogin}
        style={{ width:'100%', padding:10, background:'#1E3A5F', color:'white', border:'none', cursor:'pointer' }}>
        Login
      </button>
      <p>No account? <a href='/register'>Register</a></p>
    </div>
  );
}
