import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Navbar() {
  const { token, user, logout } = useAuth();

  return (
    <nav className="border-b border-border bg-bg-card/50 backdrop-blur-sm sticky top-0 z-50">
      <div className="max-w-5xl mx-auto px-4 h-14 flex items-center justify-between">
        <Link to="/topicos" className="text-text-bright font-bold text-lg tracking-tight hover:text-green transition-colors">
          {'>'} ForumHub<span className="text-green">_</span>
        </Link>

        <div className="flex items-center gap-4 text-sm">
          {token ? (
            <>
              <span className="text-text-muted hidden sm:inline">
                <span className="text-blue">@</span>{user?.login}
              </span>
              <button
                onClick={logout}
                className="text-text-muted hover:text-red transition-colors cursor-pointer"
              >
                logout()
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-text-muted hover:text-text-bright transition-colors">
                login
              </Link>
              <Link
                to="/register"
                className="text-bg-card bg-green hover:bg-green/80 px-3 py-1 rounded transition-colors"
              >
                register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
