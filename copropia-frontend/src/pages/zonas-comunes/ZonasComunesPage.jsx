import { useState } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import Header from '../../components/layout/Header';
import Card from '../../components/common/Card';
import Table from '../../components/common/Table';
import Button from '../../components/common/Button';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Badge from '../../components/common/Badge';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';
import { useAuth } from '../../hooks/useAuth';
import { useFetch } from '../../hooks/useFetch';
import zonaComunService from '../../api/zonaComunService';
import { formatTime } from '../../utils/formatters';

const tipoReservaOptions = [
  { value: 'POR_HORAS', label: 'Por Horas' },
  { value: 'DIA_COMPLETO', label: 'Dia Completo' },
];

export default function ZonasComunesPage() {
  const { user, isAdmin } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: zonas, loading, refetch } = useFetch(
    () => zonaComunService.getByCopropiedad(copId),
    [copId]
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({
    nombre: '',
    descripcion: '',
    capacidad: '',
    tipoReserva: 'POR_HORAS',
    maxHorasReserva: '4',
    horaApertura: '08:00',
    horaCierre: '22:00',
    requiereAprobacion: false,
  });

  const openCreate = () => {
    setEditing(null);
    setForm({
      nombre: '',
      descripcion: '',
      capacidad: '',
      tipoReserva: 'POR_HORAS',
      maxHorasReserva: '4',
      horaApertura: '08:00',
      horaCierre: '22:00',
      requiereAprobacion: false,
    });
    setModalOpen(true);
  };

  const openEdit = (zona) => {
    setEditing(zona);
    setForm({
      nombre: zona.nombre,
      descripcion: zona.descripcion || '',
      capacidad: zona.capacidad,
      tipoReserva: zona.tipoReserva,
      maxHorasReserva: zona.maxHorasReserva || '',
      horaApertura: zona.horaApertura || '08:00',
      horaCierre: zona.horaCierre || '22:00',
      requiereAprobacion: zona.requiereAprobacion,
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      copropiedadId: copId,
      nombre: form.nombre,
      descripcion: form.descripcion,
      capacidad: Number(form.capacidad),
      tipoReserva: form.tipoReserva,
      maxHorasReserva:
        form.tipoReserva === 'POR_HORAS' ? Number(form.maxHorasReserva) : null,
      horaApertura:
        form.tipoReserva === 'POR_HORAS' ? form.horaApertura : null,
      horaCierre:
        form.tipoReserva === 'POR_HORAS' ? form.horaCierre : null,
      requiereAprobacion: form.requiereAprobacion,
    };
    try {
      if (editing) {
        await zonaComunService.update(editing.id, payload);
        toast.success('Zona actualizada');
      } else {
        await zonaComunService.create(payload);
        toast.success('Zona creada');
      }
      setModalOpen(false);
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error al guardar');
    }
  };

  const handleDeactivate = async (id) => {
    if (!confirm('Desactivar esta zona comun?')) return;
    try {
      await zonaComunService.deactivate(id);
      toast.success('Zona desactivada');
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error');
    }
  };

  const columns = [
    { key: 'nombre', label: 'Nombre' },
    {
      key: 'tipoReserva',
      label: 'Tipo Reserva',
      render: (row) => <Badge text={row.tipoReserva} />,
    },
    {
      key: 'capacidad',
      label: 'Capacidad',
      render: (row) => `${row.capacidad} personas`,
    },
    {
      key: 'horario',
      label: 'Horario',
      render: (row) =>
        row.tipoReserva === 'DIA_COMPLETO'
          ? 'Todo el dia'
          : `${formatTime(row.horaApertura)} - ${formatTime(row.horaCierre)}`,
    },
    {
      key: 'maxHoras',
      label: 'Max Horas',
      render: (row) => (row.maxHorasReserva ? `${row.maxHorasReserva}h` : '-'),
    },
    {
      key: 'aprobacion',
      label: 'Aprobacion',
      render: (row) =>
        row.requiereAprobacion ? (
          <Badge text="PENDIENTE" />
        ) : (
          <span className="text-gray-400 text-xs">Automatica</span>
        ),
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (row) =>
        isAdmin() && (
          <div className="flex gap-1">
            <button
              onClick={() => openEdit(row)}
              className="p-1.5 hover:bg-gray-100 rounded-lg"
            >
              <Pencil className="w-4 h-4 text-gray-500" />
            </button>
            <button
              onClick={() => handleDeactivate(row.id)}
              className="p-1.5 hover:bg-danger-50 rounded-lg"
            >
              <Trash2 className="w-4 h-4 text-danger-500" />
            </button>
          </div>
        ),
    },
  ];

  if (loading) return <Loading />;

  return (
    <div>
      <Header
        title="Zonas Comunes"
        subtitle="Gestion de espacios compartidos"
        actions={
          isAdmin() && (
            <Button onClick={openCreate}>
              <Plus className="w-4 h-4" /> Nueva Zona
            </Button>
          )
        }
      />
      <Card>
        {zonas?.length > 0 ? (
          <Table columns={columns} data={zonas} />
        ) : (
          <EmptyState
            title="Sin zonas comunes"
            actionLabel={isAdmin() ? 'Crear' : undefined}
            onAction={isAdmin() ? openCreate : undefined}
          />
        )}
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Zona Comun' : 'Nueva Zona Comun'}
        size="lg"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Nombre"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              placeholder="Salon Comunal"
              required
            />
            <Input
              label="Capacidad"
              type="number"
              value={form.capacidad}
              onChange={(e) => setForm({ ...form, capacidad: e.target.value })}
              placeholder="50"
              required
            />
          </div>
          <Input
            label="Descripcion"
            value={form.descripcion}
            onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
          />
          <Select
            label="Tipo de Reserva"
            value={form.tipoReserva}
            onChange={(e) => setForm({ ...form, tipoReserva: e.target.value })}
            options={tipoReservaOptions}
            required
          />
          {form.tipoReserva === 'POR_HORAS' && (
            <div className="grid grid-cols-3 gap-4">
              <Input
                label="Max Horas por Reserva"
                type="number"
                value={form.maxHorasReserva}
                onChange={(e) =>
                  setForm({ ...form, maxHorasReserva: e.target.value })
                }
                required
              />
              <Input
                label="Hora Apertura"
                type="time"
                value={form.horaApertura}
                onChange={(e) =>
                  setForm({ ...form, horaApertura: e.target.value })
                }
                required
              />
              <Input
                label="Hora Cierre"
                type="time"
                value={form.horaCierre}
                onChange={(e) =>
                  setForm({ ...form, horaCierre: e.target.value })
                }
                required
              />
            </div>
          )}
          <label className="flex items-center gap-2 cursor-pointer">
            <input
              type="checkbox"
              checked={form.requiereAprobacion}
              onChange={(e) =>
                setForm({ ...form, requiereAprobacion: e.target.checked })
              }
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">
              Requiere aprobacion del administrador
            </span>
          </label>
          <div className="flex justify-end gap-2 pt-4">
            <Button
              variant="secondary"
              type="button"
              onClick={() => setModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button type="submit">{editing ? 'Actualizar' : 'Crear'}</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
