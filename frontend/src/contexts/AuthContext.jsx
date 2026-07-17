import { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(false);

  function saveAuth(tokenValue, login) {
    localStorage.setItem('token', tokenValue);
    localStorage.setItem('user', JSON.stringify({ login }));
    setToken(tokenValue);
    setUser({ login });
  }

  async function login(login, senha) {
    setLoading(true);
    try {
      const data = await apiLogin(login, senha);
      saveAuth(data.token, login);
      return true;
    } catch {
      return false;
    } finally {
      setLoading(false);
    }
  }

  async function register(login, senha, nomeExibicao) {
    setLoading(true);
    try {
      await apiRegister(login, senha, nomeExibicao);
      return true;
    } catch {
      return false;
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ token, user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
