export default function LoadingState({ texto = 'Carregando...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-text-muted">
      <div className="w-5 h-5 border-2 border-border border-t-green rounded-full animate-spin mb-3" />
      <span className="text-sm font-mono">{texto}</span>
    </div>
  );
}
