import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { getTopico, deleteTopico } from '../api/topicos';
import { getRespostas, createResposta } from '../api/respostas';
import LoadingState from '../components/LoadingState';
import EmptyState from '../components/EmptyState';

function formatDate(dateStr) {
  const d = new Date(dateStr);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

export default function TopicoDetalhe() {
  const { id } = useParams();
  const { token, user } = useAuth();
  const navigate = useNavigate();

  const [topico, setTopico] = useState(null);
  const [respostas, setRespostas] = useState([]);
  const [mensagem, setMensagem] = useState('');
  const [loading, setLoading] = useState(true);
  const [loadingRespostas, setLoadingRespostas] = useState(true);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const isAuthor = user && topico && user.login === topico.autor;

  useEffect(() => {
    setLoading(true);
    getTopico(id)
      .then(setTopico)
      .catch(() => setError('Erro ao carregar tópico.'))
      .finally(() => setLoading(false));

    setLoadingRespostas(true);
    getRespostas(id)
      .then(setRespostas)
      .catch(() => {})
      .finally(() => setLoadingRespostas(false));
  }, [id]);

  async function handleDelete() {
    if (!window.confirm('Tem certeza que deseja deletar este tópico?')) return;
    try {
      await deleteTopico(id);
      navigate('/topicos');
    } catch {
      setError('Erro ao deletar tópico.');
    }
  }

  async function handleReply(e) {
    e.preventDefault();
    if (!mensagem.trim()) return;
    setSubmitting(true);
    try {
      const nova = await createResposta(id, { mensagem });
      setRespostas((prev) => [...prev, nova]);
      setMensagem('');
    } catch {
      setError('Erro ao enviar resposta.');
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) return <LoadingState />;
  if (error && !topico) return <p className="text-red text-sm font-mono text-center py-16">// {error}</p>;
  if (!topico) return null;

  return (
    <div className="flex-1 max-w-3xl w-full mx-auto px-4 py-8">
      <Link to="/topicos" className="text-text-muted text-sm font-mono hover:text-text-bright transition-colors">
        {'<-'} voltar
      </Link>

      <div className="mt-6 border border-border rounded-md bg-bg-card p-6">
        <div className="flex items-start justify-between gap-3">
          <h1 className="font-serif text-xl text-text-bright leading-snug">{topico.titulo}</h1>
          <div className="flex items-center gap-1.5 shrink-0 mt-1">
            <span className={`w-2 h-2 rounded-full ${topico.status === 'ATIVO' ? 'bg-green' : 'bg-red'}`} />
            <span className="text-xs text-text-muted font-mono">{topico.status}</span>
          </div>
        </div>

        <p className="text-text mt-3 text-sm leading-relaxed">{topico.mensagem}</p>

        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-4 text-xs text-text-muted">
          <span><span className="text-blue">@</span>{topico.autor}</span>
          <span className="border border-border px-1.5 py-0.5 rounded text-green/80">{topico.curso}</span>
          <span className="ml-auto">{formatDate(topico.dataCriacao)}</span>
        </div>

        {isAuthor && (
          <div className="flex gap-2 mt-4 pt-4 border-t border-border-light">
            <Link
              to={`/topicos/${id}/editar`}
              className="text-xs font-mono border border-border px-3 py-1 rounded text-text-muted hover:text-text-bright hover:border-text-muted transition-colors"
            >
              editar()
            </Link>
            <button
              onClick={handleDelete}
              className="text-xs font-mono border border-red-dim/50 px-3 py-1 rounded text-red hover:bg-red/10 transition-colors cursor-pointer"
            >
              deletar()
            </button>
          </div>
        )}
      </div>

      <div className="mt-8">
        <h2 className="font-mono text-sm text-text-muted mb-4">
          {'// '}{respostas.length} {respostas.length === 1 ? 'resposta' : 'respostas'}
        </h2>

        {loadingRespostas && <LoadingState texto="Carregando respostas..." />}

        {!loadingRespostas && respostas.length === 0 && (
          <EmptyState mensagem="Nenhuma resposta ainda. Seja o primeiro!" />
        )}

        <div className="space-y-3">
          {respostas.map((r) => (
            <div key={r.id} className="border border-border rounded-md bg-bg-card p-4">
              <p className="text-text text-sm leading-relaxed">{r.mensagem}</p>
              <div className="flex items-center gap-3 mt-3 text-xs text-text-muted">
                <span><span className="text-blue">@</span>{r.autor}</span>
                {r.solucao && (
                  <span className="text-green font-mono">[solução aceita]</span>
                )}
                <span className="ml-auto">{formatDate(r.dataCriacao)}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {token && (
        <div className="mt-8 border border-border rounded-md bg-bg-card p-4">
          <h3 className="font-mono text-sm text-text-muted mb-3">// nova resposta</h3>
          <form onSubmit={handleReply}>
            <textarea
              value={mensagem}
              onChange={(e) => setMensagem(e.target.value)}
              rows={4}
              className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors resize-none"
              placeholder="Escreva sua resposta..."
            />
            <div className="flex justify-end mt-3">
              <button
                type="submit"
                disabled={submitting || !mensagem.trim()}
                className="bg-green text-bg-card font-mono text-sm px-4 py-1.5 rounded hover:bg-green/80 disabled:opacity-50 transition-colors cursor-pointer"
              >
                {submitting ? 'enviando...' : 'responder()'}
              </button>
            </div>
          </form>
        </div>
      )}

      {!token && (
        <p className="text-center text-text-muted text-sm font-mono mt-8">
          // <Link to="/login" className="text-blue hover:text-text-bright transition-colors">faça login</Link> para responder
        </p>
      )}
    </div>
  );
}
