import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Login() {
  const [login, setLogin] = useState('');
  const [senha, setSenha] = useState('');
  const [error, setError] = useState('');
  const { login: authLogin, loading } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (!login.trim() || !senha.trim()) {
      setError('Preencha todos os campos.');
      return;
    }

    const ok = await authLogin(login, senha);
    if (ok) {
      navigate('/topicos');
    } else {
      setError('Credenciais inválidas.');
    }
  }

  return (
    <div className="flex-1 flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <h1 className="font-serif text-2xl text-text-bright">Entrar</h1>
          <p className="text-text-muted text-sm mt-1 font-mono">// autentique-se para continuar</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs text-text-muted mb-1 font-mono">login</label>
            <input
              type="text"
              value={login}
              onChange={(e) => setLogin(e.target.value)}
              className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors"
              placeholder="seu@email.com"
            />
          </div>
          <div>
            <label className="block text-xs text-text-muted mb-1 font-mono">senha</label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors"
              placeholder="••••••"
            />
          </div>

          {error && (
            <p className="text-red text-sm font-mono">// {error}</p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green text-bg-card font-mono text-sm py-2 rounded hover:bg-green/80 disabled:opacity-50 transition-colors cursor-pointer"
          >
            {loading ? 'autenticando...' : 'login()'}
          </button>
        </form>

        <p className="text-center text-text-muted text-sm mt-6 font-mono">
          não tem conta?{' '}
          <Link to="/register" className="text-blue hover:text-text-bright transition-colors">
            registrar
          </Link>
        </p>
      </div>
    </div>
  );
}
