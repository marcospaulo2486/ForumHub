export default function EmptyState({ mensagem = 'Nenhum item encontrado.' }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-text-muted">
      <span className="text-2xl mb-2">{'{ }'}</span>
      <span className="text-sm font-mono">{mensagem}</span>
    </div>
  );
}
