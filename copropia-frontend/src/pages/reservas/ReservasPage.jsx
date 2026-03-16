import { useState, useEffect } from 'react';
import { Plus, Check, X, CalendarClock } from 'lucide-react';
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
import reservaService from '../../api/reservaService';
import zonaComunService from '../../api/zonaComunService';
import { formatDate, formatTime } from '../../utils/formatters';

export default function ReservasPage() {
  const { user, isAdmin } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: zonas } = useFetch(
    () => zonaComunService.getByCopropiedad(copId),
    [copId]
  );
  const [selectedZona, setSelectedZona] = useState('');
  const [selectedFecha, setSelectedFecha] = useState(
    new Date().toISOString().split('T')[0]
  );
  const [reservas, setReservas] = useState([]);
  const [misReservas, setMisReservas] = useState([]);
  const [loadingReservas, setLoadingReservas] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({
    zonaComunId: '',
    fecha: '',
    horaInicio: '',
    horaFin: '',
    observaciones: '',
    propiedadId: '',
  });

  const loadReservas = async () => {
    if (!selectedZona || !selectedFecha) return;
    setLoadingReservas(true);
    try {
      const res = await reservaService.getByZonaAndFecha(
        selectedZona,
        selectedFecha
      );
      setReservas(res.data || []);
    } catch {
      setReservas([]);
    } finally {
      setLoadingReservas(false);
    }
  };

  useEffect(() => {
    loadReservas();
  }, [selectedZona, selectedFecha]);

  const openCreate = () => {
    setForm({
      zonaComunId: selectedZona || '',
      fecha: selectedFecha || '',
      horaInicio: '',
      horaFin: '',
      observaciones: '',
      propiedadId: '',
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await reservaService.create({
        zonaComunId: Number(form.zonaComunId),
        usuarioId: 1,
        propiedadId: Number(form.propiedadId),
        fecha: form.fecha,
        horaInicio: form.horaInicio || null,
        horaFin: form.horaFin || null,
        observaciones: form.observaciones,
      });
      toast.success('Reserva creada!');
      setModalOpen(false);
      loadReservas();
    } catch (err) {
      toast.error(err?.message || 'Error al reservar');
    }
  };

  const handleConfirm = async (id) => {
    try {
      await reservaService.confirm(id);
      toast.success('Reserva confirmada');
      loadReservas();
    } catch (err) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleCancel = async (id) => {
    if (!confirm('Cancelar esta reserva?')) return;
    try {
      await reservaService.cancel(id, 1);
      toast.success('Reserva cancelada');
      loadReservas();
    } catch (err) {
      toast.error(err?.message || 'Error');
    }
  };

  const selectedZonaObj = zonas?.find((z) => z.id === Number(selectedZona));

  const columns = [
    {
      key: 'fecha',
      label: 'Fecha',
      render: (row) => formatDate(row.fecha),
    },
    {
      key: 'horario',
      label: 'Horario',
      render: (row) =>
        `${formatTime(row.horaInicio)} - ${formatTime(row.horaFin)}`,
    },
    {
      key: 'estado',
      label: 'Estado',
      render: (row) => <Badge text={row.estado} />,
    },
    {
      key: 'propiedadId',
      label: 'Propiedad',
      render: (row) => `#${row.propiedadId}`,
    },
    {
      key: 'observaciones',
      label: 'Observaciones',
      render: (row) => row.observaciones || '-',
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (row) => (
        <div className="flex gap-1">
          {row.estado === 'PENDIENTE' && isAdmin() && (
            <button
              onClick={() => handleConfirm(row.id)}
              className="p-1.5 hover:bg-success-50 rounded-lg"
              title="Confirmar"
            >
              <Check className="w-4 h-4 text-success-500" />
            </button>
          )}
          {(row.estado === 'PENDIENTE' || row.estado === 'CONFIRMADA') && (
            <button
              onClick={() => handleCancel(row.id)}
              className="p-1.5 hover:bg-danger-50 rounded-lg"
              title="Cancelar"
            >
              <X className="w-4 h-4 text-danger-500" />
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div>
      <Header
        title="Reservas"
        subtitle="Agendamiento de zonas comunes"
        actions={
          <Button onClick={openCreate}>
            <Plus className="w-4 h-4" /> Nueva Reserva
          </Button>
        }
      />

      <Card className="mb-6">
        <div className="flex items-end gap-4">
          <Select
            label="Zona Comun"
            value={selectedZona}
            onChange={(e) => setSelectedZona(e.target.value)}
            options={(zonas || []).map((z) => ({
              value: z.id,
              label: `${z.nombre} (${z.tipoReserva === 'DIA_COMPLETO' ? 'Dia completo' : 'Por horas'})`,
            }))}
            className="flex-1"
          />
          <Input
            label="Fecha"
            type="date"
            value={selectedFecha}
            onChange={(e) => setSelectedFecha(e.target.value)}
          />
        </div>
      </Card>

      {selectedZona && (
        <Card
          title={`Reservas - ${selectedZonaObj?.nombre || ''}`}
          subtitle={selectedFecha}
        >
          {loadingReservas ? (
            <Loading />
          ) : reservas.length > 0 ? (
            <Table columns={columns} data={reservas} />
          ) : (
            <EmptyState
              title="Sin reservas"
              description="No hay reservas para esta fecha"
            />
          )}
        </Card>
      )}

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Nueva Reserva"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select
            label="Zona Comun"
            value={form.zonaComunId}
            onChange={(e) => setForm({ ...form, zonaComunId: e.target.value })}
            options={(zonas || []).map((z) => ({
              value: z.id,
              label: z.nombre,
            }))}
            required
          />
          <Input
            label="Fecha"
            type="date"
            value={form.fecha}
            onChange={(e) => setForm({ ...form, fecha: e.target.value })}
            required
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Hora Inicio"
              type="time"
              value={form.horaInicio}
              onChange={(e) => setForm({ ...form, horaInicio: e.target.value })}
            />
            <Input
              label="Hora Fin"
              type="time"
              value={form.horaFin}
              onChange={(e) => setForm({ ...form, horaFin: e.target.value })}
            />
          </div>
          <Input
            label="ID Propiedad"
            type="number"
            value={form.propiedadId}
            onChange={(e) => setForm({ ...form, propiedadId: e.target.value })}
            required
          />
          <Input
            label="Observaciones"
            value={form.observaciones}
            onChange={(e) =>
              setForm({ ...form, observaciones: e.target.value })
            }
            placeholder="Opcional"
          />
          <div className="flex justify-end gap-2 pt-4">
            <Button
              variant="secondary"
              type="button"
              onClick={() => setModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button type="submit">Reservar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
