import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import TopicosList from './pages/TopicosList';
import TopicoDetalhe from './pages/TopicoDetalhe';
import TopicoForm from './pages/TopicoForm';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/topicos" element={<TopicosList />} />
          <Route path="/topicos/novo" element={<ProtectedRoute><TopicoForm /></ProtectedRoute>} />
          <Route path="/topicos/:id" element={<TopicoDetalhe />} />
          <Route path="/topicos/:id/editar" element={<ProtectedRoute><TopicoForm /></ProtectedRoute>} />
          <Route path="*" element={<Login />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
