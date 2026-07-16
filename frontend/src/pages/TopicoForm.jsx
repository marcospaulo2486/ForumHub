import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { createTopico, getTopico, updateTopico } from '../api/topicos';

export default function TopicoForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  const [titulo, setTitulo] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [curso, setCurso] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(isEdit);

  useEffect(() => {
    if (!isEdit) return;
    getTopico(id)
      .then((t) => {
        setTitulo(t.titulo);
        setMensagem(t.mensagem);
        setCurso(t.curso);
      })
      .catch(() => setError('Erro ao carregar tópico.'))
      .finally(() => setLoadingData(false));
  }, [id, isEdit]);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (!titulo.trim() || !mensagem.trim() || !curso.trim()) {
      setError('Preencha todos os campos.');
      return;
    }

    setLoading(true);
    try {
      const payload = { titulo, mensagem, curso };
      if (isEdit) {
        await updateTopico(id, payload);
        navigate(`/topicos/${id}`);
      } else {
        const novo = await createTopico(payload);
        navigate(`/topicos/${novo.id}`);
      }
    } catch {
      setError(isEdit ? 'Erro ao atualizar tópico.' : 'Erro ao criar tópico.');
    } finally {
      setLoading(false);
    }
  }

  if (loadingData) {
    return (
      <div className="flex-1 flex items-center justify-center py-16">
        <div className="w-5 h-5 border-2 border-border border-t-green rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="flex-1 max-w-2xl w-full mx-auto px-4 py-8">
      <Link to={isEdit ? `/topicos/${id}` : '/topicos'} className="text-text-muted text-sm font-mono hover:text-text-bright transition-colors">
        {'<-'} voltar
      </Link>

      <div className="mt-6">
        <h1 className="font-serif text-2xl text-text-bright">
          {isEdit ? 'Editar tópico' : 'Novo tópico'}
        </h1>
        <p className="text-text-muted text-sm font-mono mt-1">
          {'// '}{isEdit ? 'atualize as informações' : 'compartilhe sua dúvida ou conhecimento'}
        </p>
      </div>

      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        <div>
          <label className="block text-xs text-text-muted mb-1 font-mono">titulo</label>
          <input
            type="text"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors"
            placeholder="Como usar Optional no Java?"
          />
        </div>
        <div>
          <label className="block text-xs text-text-muted mb-1 font-mono">mensagem</label>
          <textarea
            value={mensagem}
            onChange={(e) => setMensagem(e.target.value)}
            rows={6}
            className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors resize-none"
            placeholder="Descreva sua dúvida ou tópico em detalhes..."
          />
        </div>
        <div>
          <label className="block text-xs text-text-muted mb-1 font-mono">curso</label>
          <input
            type="text"
            value={curso}
            onChange={(e) => setCurso(e.target.value)}
            className="w-full bg-bg-input border border-border rounded px-3 py-2 text-text-bright text-sm font-mono focus:outline-none focus:border-green transition-colors"
            placeholder="Java, Spring Boot, React..."
          />
        </div>

        {error && (
          <p className="text-red text-sm font-mono">// {error}</p>
        )}

        <div className="flex justify-end pt-2">
          <button
            type="submit"
            disabled={loading}
            className="bg-green text-bg-card font-mono text-sm px-6 py-2 rounded hover:bg-green/80 disabled:opacity-50 transition-colors cursor-pointer"
          >
            {loading ? 'salvando...' : isEdit ? 'atualizar()' : 'criar()'}
          </button>
        </div>
      </form>
    </div>
  );
}
