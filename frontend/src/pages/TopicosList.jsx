import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getTopicos } from '../api/topicos';
import TopicoCard from '../components/TopicoCard';
import LoadingState from '../components/LoadingState';
import EmptyState from '../components/EmptyState';

export default function TopicosList() {
  const [topicos, setTopicos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { token } = useAuth();

  useEffect(() => {
    getTopicos()
      .then(setTopicos)
      .catch(() => setError('Erro ao carregar tópicos.'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="flex-1 max-w-5xl w-full mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="font-serif text-2xl text-text-bright">Tópicos</h1>
          <p className="text-text-muted text-sm font-mono mt-1">// {topicos.length} tópicos encontrados</p>
        </div>
        {token && (
          <Link
            to="/topicos/novo"
            className="bg-green text-bg-card font-mono text-sm px-4 py-2 rounded hover:bg-green/80 transition-colors"
          >
            + novo tópico
          </Link>
        )}
      </div>

      {loading && <LoadingState />}
      {error && <p className="text-red text-sm font-mono text-center py-8">// {error}</p>}
      {!loading && !error && topicos.length === 0 && (
        <EmptyState mensagem="Nenhum tópico encontrado. Crie o primeiro!" />
      )}
      {!loading && !error && topicos.length > 0 && (
        <div className="space-y-3">
          {topicos.map((t) => (
            <TopicoCard key={t.id} topico={t} />
          ))}
        </div>
      )}
    </div>
  );
}
