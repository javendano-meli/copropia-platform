import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import Layout from './components/layout/Layout';
import LoginPage from './pages/auth/LoginPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import CopropiedadesPage from './pages/copropiedades/CopropiedadesPage';
import PropiedadesPage from './pages/propiedades/PropiedadesPage';
import UsuariosPage from './pages/usuarios/UsuariosPage';
import AsambleasPage from './pages/asambleas/AsambleasPage';
import VotacionesPage from './pages/votaciones/VotacionesPage';
import ZonasComunesPage from './pages/zonas-comunes/ZonasComunesPage';
import ReservasPage from './pages/reservas/ReservasPage';

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return <Layout>{children}</Layout>;
}

export default function App() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route path="/" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="/copropiedades" element={<ProtectedRoute><CopropiedadesPage /></ProtectedRoute>} />
      <Route path="/propiedades" element={<ProtectedRoute><PropiedadesPage /></ProtectedRoute>} />
      <Route path="/usuarios" element={<ProtectedRoute><UsuariosPage /></ProtectedRoute>} />
      <Route path="/asambleas" element={<ProtectedRoute><AsambleasPage /></ProtectedRoute>} />
      <Route path="/votaciones" element={<ProtectedRoute><VotacionesPage /></ProtectedRoute>} />
      <Route path="/zonas-comunes" element={<ProtectedRoute><ZonasComunesPage /></ProtectedRoute>} />
      <Route path="/reservas" element={<ProtectedRoute><ReservasPage /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
