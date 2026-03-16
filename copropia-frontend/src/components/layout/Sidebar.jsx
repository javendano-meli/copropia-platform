import { NavLink } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import {
  LayoutDashboard, Building2, Home, Users, CalendarDays,
  Vote, TreePalm, CalendarClock, MessageSquare, LogOut, Settings
} from 'lucide-react';

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/copropiedades', icon: Building2, label: 'Copropiedades', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD'] },
  { to: '/propiedades', icon: Home, label: 'Propiedades', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/usuarios', icon: Users, label: 'Usuarios', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD'] },
  { to: '/asambleas', icon: CalendarDays, label: 'Asambleas', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/votaciones', icon: Vote, label: 'Votaciones', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/zonas-comunes', icon: TreePalm, label: 'Zonas Comunes', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/reservas', icon: CalendarClock, label: 'Reservas', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
  { to: '/pqr', icon: MessageSquare, label: 'PQR', roles: ['SUPER_ADMIN', 'ADMIN_COPROPIEDAD', 'PROPIETARIO'] },
];

export default function Sidebar() {
  const { user, logout } = useAuth();

  const filteredItems = navItems.filter((item) => item.roles.includes(user?.rol));

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-white border-r border-gray-200 flex flex-col z-40">
      <div className="px-6 py-5 border-b border-gray-100">
        <h1 className="text-xl font-bold text-primary-700">Copropia</h1>
        <p className="text-xs text-gray-400 mt-0.5">Gestion de Copropiedades</p>
      </div>

      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {filteredItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary-50 text-primary-700'
                  : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
              }`
            }
          >
            <item.icon className="w-5 h-5" />
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="px-3 py-4 border-t border-gray-100">
        <div className="px-3 py-2 mb-2">
          <p className="text-sm font-medium text-gray-900 truncate">{user?.email}</p>
          <p className="text-xs text-gray-400">{user?.rol?.replace('_', ' ')}</p>
        </div>
        <button
          onClick={logout}
          className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium text-gray-600 hover:bg-danger-50 hover:text-danger-700 transition-colors"
        >
          <LogOut className="w-5 h-5" />
          Cerrar Sesion
        </button>
      </div>
    </aside>
  );
}
