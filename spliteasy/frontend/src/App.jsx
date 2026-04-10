import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Login      from './pages/Login';
import Register   from './pages/Register';
import Dashboard  from './pages/Dashboard';
import GroupDetail from './pages/GroupDetail';
import SettleUp   from './pages/SettleUp';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path='/'              element={<Navigate to='/dashboard'/>} />
          <Route path='/login'         element={<Login/>} />
          <Route path='/register'      element={<Register/>} />
          <Route path='/dashboard'     element={<Dashboard/>} />
          <Route path='/groups/:id'    element={<GroupDetail/>} />
          <Route path='/groups/:id/settle' element={<SettleUp/>} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
