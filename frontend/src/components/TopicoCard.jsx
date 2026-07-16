import { Link } from 'react-router-dom';

function formatDate(dateStr) {
  const d = new Date(dateStr);
  return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });
}

export default function TopicoCard({ topico }) {
  const isActive = topico.status === 'ATIVO';

  return (
    <Link
      to={`/topicos/${topico.id}`}
      className="block border border-border rounded-md p-4 bg-bg-card hover:border-text-muted transition-colors group"
    >
      <div className="flex items-start justify-between gap-3">
        <h3 className="font-serif text-lg text-text-bright group-hover:text-green transition-colors leading-snug">
          {topico.titulo}
        </h3>
        <div className="flex items-center gap-1.5 shrink-0 mt-1">
          <span className={`w-2 h-2 rounded-full ${isActive ? 'bg-green' : 'bg-red'}`} />
          <span className="text-xs text-text-muted font-mono">{topico.status}</span>
        </div>
      </div>

      <p className="text-text-muted text-sm mt-2 line-clamp-2">{topico.mensagem}</p>

      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-3 text-xs text-text-muted">
        <span>
          <span className="text-blue">@</span>{topico.autor}
        </span>
        <span className="border border-border px-1.5 py-0.5 rounded text-green/80">
          {topico.curso}
        </span>
        <span className="ml-auto">{formatDate(topico.dataCriacao)}</span>
      </div>
    </Link>
  );
}
