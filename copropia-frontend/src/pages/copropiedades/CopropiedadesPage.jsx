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
import { useFetch } from '../../hooks/useFetch';
import copropiedadService from '../../api/copropiedadService';
import planService from '../../api/planService';

export default function CopropiedadesPage() {
  const { data: copropiedades, loading, refetch } = useFetch(() => copropiedadService.getAll());
  const { data: planes } = useFetch(() => planService.getAll());
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({
    planId: '',
    nombre: '',
    nit: '',
    direccion: '',
    ciudad: '',
    departamento: '',
    telefono: '',
    email: '',
  });

  const openCreate = () => {
    setEditing(null);
    setForm({
      planId: '',
      nombre: '',
      nit: '',
      direccion: '',
      ciudad: '',
      departamento: '',
      telefono: '',
      email: '',
    });
    setModalOpen(true);
  };

  const openEdit = (cop) => {
    setEditing(cop);
    setForm({
      planId: cop.planId || '',
      nombre: cop.nombre,
      nit: cop.nit,
      direccion: cop.direccion,
      ciudad: cop.ciudad,
      departamento: cop.departamento || '',
      telefono: cop.telefono || '',
      email: cop.email || '',
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editing) {
        await copropiedadService.update(editing.id, form);
        toast.success('Copropiedad actualizada');
      } else {
        await copropiedadService.create({ ...form, planId: Number(form.planId) });
        toast.success('Copropiedad creada');
      }
      setModalOpen(false);
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error al guardar');
    }
  };

  const handleDeactivate = async (id) => {
    if (!confirm('Desactivar esta copropiedad?')) return;
    try {
      await copropiedadService.deactivate(id);
      toast.success('Copropiedad desactivada');
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error');
    }
  };

  const columns = [
    { key: 'nombre', label: 'Nombre' },
    { key: 'nit', label: 'NIT' },
    { key: 'ciudad', label: 'Ciudad' },
    {
      key: 'estado',
      label: 'Estado',
      render: (row) => <Badge text={row.estado} />,
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (row) => (
        <div className="flex gap-2">
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
        title="Copropiedades"
        subtitle="Gestion de conjuntos y edificios"
        actions={
          <Button onClick={openCreate}>
            <Plus className="w-4 h-4" /> Nueva Copropiedad
          </Button>
        }
      />
      <Card>
        {copropiedades?.length > 0 ? (
          <Table columns={columns} data={copropiedades} />
        ) : (
          <EmptyState
            title="Sin copropiedades"
            description="Crea tu primera copropiedad"
            actionLabel="Crear"
            onAction={openCreate}
          />
        )}
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? 'Editar Copropiedad' : 'Nueva Copropiedad'}
        size="lg"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            {!editing && (
              <Select
                label="Plan"
                value={form.planId}
                onChange={(e) => setForm({ ...form, planId: e.target.value })}
                options={(planes || []).map((p) => ({
                  value: p.id,
                  label: p.nombre,
                }))}
                required
              />
            )}
            <Input
              label="Nombre"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            {!editing && (
              <Input
                label="NIT"
                value={form.nit}
                onChange={(e) => setForm({ ...form, nit: e.target.value })}
                required
              />
            )}
            <Input
              label="Direccion"
              value={form.direccion}
              onChange={(e) => setForm({ ...form, direccion: e.target.value })}
              required
            />
            <Input
              label="Ciudad"
              value={form.ciudad}
              onChange={(e) => setForm({ ...form, ciudad: e.target.value })}
              required
            />
            <Input
              label="Departamento"
              value={form.departamento}
              onChange={(e) =>
                setForm({ ...form, departamento: e.target.value })
              }
            />
            <Input
              label="Telefono"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
            />
            <Input
              label="Email"
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
          </div>
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
