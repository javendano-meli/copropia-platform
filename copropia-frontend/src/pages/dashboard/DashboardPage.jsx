import { Building2, Home, Users, CalendarDays, Vote, TreePalm } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import Header from '../../components/layout/Header';
import StatsCard from '../../components/common/StatsCard';
import Card from '../../components/common/Card';

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div>
      <Header
        title={`Bienvenido${user?.email ? ', ' + user.email : ''}`}
        subtitle="Panel de administracion de Copropia"
      />

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <StatsCard title="Copropiedades" value="-" icon={Building2} color="primary" />
        <StatsCard title="Propiedades" value="-" icon={Home} color="success" />
        <StatsCard title="Usuarios" value="-" icon={Users} color="warning" />
        <StatsCard title="Asambleas" value="-" icon={CalendarDays} color="primary" />
        <StatsCard title="Votaciones Activas" value="-" icon={Vote} color="success" />
        <StatsCard title="Zonas Comunes" value="-" icon={TreePalm} color="warning" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Actividad Reciente" subtitle="Ultimas acciones en la plataforma">
          <div className="text-sm text-gray-400 text-center py-8">
            Conecta con el backend para ver la actividad
          </div>
        </Card>
        <Card title="Proximas Asambleas" subtitle="Asambleas programadas">
          <div className="text-sm text-gray-400 text-center py-8">
            Conecta con el backend para ver las asambleas
          </div>
        </Card>
      </div>
    </div>
  );
}
