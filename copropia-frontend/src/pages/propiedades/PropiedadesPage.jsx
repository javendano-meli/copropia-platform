import { useState } from 'react';
import { Plus, Pencil } from 'lucide-react';
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
import propiedadService from '../../api/propiedadService';

const tipoOptions = [
  { value: 'APARTAMENTO', label: 'Apartamento' },
  { value: 'CASA', label: 'Casa' },
  { value: 'LOCAL', label: 'Local' },
  { value: 'OFICINA', label: 'Oficina' },
  { value: 'PARQUEADERO', label: 'Parqueadero' },
  { value: 'DEPOSITO', label: 'Deposito' },
  { value: 'OTRO', label: 'Otro' },
];

export default function PropiedadesPage() {
  const { user } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: propiedades, loading, refetch } = useFetch(
    () => propiedadService.getByCopropiedad(copId),
    [copId]
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({
    copropiedadId: copId,
    propietarioId: '',
    identificacion: '',
    tipo: 'APARTAMENTO',
    metrosCuadrados: '',
    coeficiente: '',
  });

  const openCreate = () => {
    setEditing(null);
    setForm({
      copropiedadId: copId,
      propietarioId: '',
      identificacion: '',
      tipo: 'APARTAMENTO',
      metrosCuadrados: '',
      coeficiente: '',
    });
    setModalOpen(true);
  };

  const openEdit = (prop) => {
    setEditing(prop);
    setForm({
      copropiedadId: prop.copropiedadId,
      propietarioId: prop.propietarioId || '',
      identificacion: prop.identificacion,
      tipo: prop.tipo,
      metrosCuadrados: prop.metrosCuadrados || '',
      coeficiente: prop.coeficiente,
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...form,
      copropiedadId: Number(form.copropiedadId),
      propietarioId: form.propietarioId ? Number(form.propietarioId) : null,
      metrosCuadrados: Number(form.metrosCuadrados),
      coeficiente: Number(form.coeficiente),
    };
    try {
      if (editing) {
        await propiedadService.update(editing.id, payload);
        toast.success('Propiedad actualizada');
      } else {
        await propiedadService.create(payload);
        toast.success('Propiedad creada');
      }
      setModalOpen(false);
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error al guardar');
    }
  };

  const totalCoef =
    propiedades?.reduce((sum, p) => sum + (p.coeficiente || 0), 0) || 0;

  const columns = [
    { key: 'identificacion', label: 'Identificacion' },
    {
      key: 'tipo',
      label: 'Tipo',
      render: (row) => <Badge text={row.tipo} />,
    },
    {
      key: 'metrosCuadrados',
      label: 'M2',
      render: (row) =>
        row.metrosCuadrados ? `${row.metrosCuadrados} m2` : '-',
    },
    {
      key: 'coeficiente',
      label: 'Coeficiente',
      render: (row) => `${row.coeficiente}%`,
    },
    {
      key: 'estado',
      label: 'Estado',
      render: (row) => (
        <Badge text={row.estado?.toUpperCase() || 'ACTIVA'} />
      ),
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (row) => (
        <button
          onClick={() => openEdit(row)}
          className="p-1.5 hover:bg-gray-100 rounded-lg"
        >
          <Pencil className="w-4 h-4 text-gray-500" />
        </button>
      ),
    },
  ];

  if (loading) return <Loading />;

  return (
    <div>
      <Header
        title="Propiedades"
        subtitle={`Coeficiente total: ${totalCoef.toFixed(4)}%`}
        actions={
          <Button onClick={openCreate}>
            <Plus className="w-4 h-4" /> Nueva Propiedad
          </Button>
        }
      />
      <Card>
        {propiedades?.length > 0 ? (
          <Table columns={columns} data={propiedades} />
        ) : (
          <EmptyState
            title="Sin propiedades"
            actionLabel="Crear"
            onAction={openCreate}
          />
        )}
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Propiedad' : 'Nueva Propiedad'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Identificacion"
            value={form.identificacion}
            onChange={(e) =>
              setForm({ ...form, identificacion: e.target.value })
            }
            placeholder="Apto 101"
            required
          />
          <Select
            label="Tipo"
            value={form.tipo}
            onChange={(e) => setForm({ ...form, tipo: e.target.value })}
            options={tipoOptions}
            required
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Metros Cuadrados"
              type="number"
              step="0.01"
              value={form.metrosCuadrados}
              onChange={(e) =>
                setForm({ ...form, metrosCuadrados: e.target.value })
              }
            />
            <Input
              label="Coeficiente (%)"
              type="number"
              step="0.0001"
              value={form.coeficiente}
              onChange={(e) =>
                setForm({ ...form, coeficiente: e.target.value })
              }
              required
            />
          </div>
          <Input
            label="ID Propietario"
            type="number"
            value={form.propietarioId}
            onChange={(e) =>
              setForm({ ...form, propietarioId: e.target.value })
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
            <Button type="submit">
              {editing ? 'Actualizar' : 'Crear'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
