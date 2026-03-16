import { useState } from 'react';
import { Plus, UserX } from 'lucide-react';
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
import authService from '../../api/authService';

const rolOptions = [
  { value: 'ADMIN_COPROPIEDAD', label: 'Administrador' },
  { value: 'PROPIETARIO', label: 'Propietario' },
];

export default function UsuariosPage() {
  const { user } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: usuarios, loading, refetch } = useFetch(
    () => authService.getUsuariosByCopropiedad(copId),
    [copId]
  );
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    telefono: '',
    rol: 'PROPIETARIO',
    copropiedadId: copId,
  });

  const openCreate = () => {
    setForm({
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      telefono: '',
      rol: 'PROPIETARIO',
      copropiedadId: copId,
    });
    setModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authService.register({
        ...form,
        copropiedadId: Number(form.copropiedadId),
      });
      toast.success('Usuario registrado');
      setModalOpen(false);
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error al registrar');
    }
  };

  const handleDeactivate = async (id) => {
    if (!confirm('Desactivar este usuario?')) return;
    try {
      await authService.deactivateUsuario(id);
      toast.success('Usuario desactivado');
      refetch();
    } catch (err) {
      toast.error(err?.message || 'Error');
    }
  };

  const columns = [
    {
      key: 'nombre',
      label: 'Nombre',
      render: (row) => `${row.nombre} ${row.apellido}`,
    },
    { key: 'email', label: 'Email' },
    {
      key: 'rol',
      label: 'Rol',
      render: (row) => <Badge text={row.rol} />,
    },
    {
      key: 'activo',
      label: 'Estado',
      render: (row) => (
        <Badge text={row.activo ? 'ACTIVA' : 'INACTIVA'} />
      ),
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (row) =>
        row.activo && (
          <button
            onClick={() => handleDeactivate(row.id)}
            className="p-1.5 hover:bg-danger-50 rounded-lg"
          >
            <UserX className="w-4 h-4 text-danger-500" />
          </button>
        ),
    },
  ];

  if (loading) return <Loading />;

  return (
    <div>
      <Header
        title="Usuarios"
        subtitle="Gestion de usuarios de la copropiedad"
        actions={
          <Button onClick={openCreate}>
            <Plus className="w-4 h-4" /> Nuevo Usuario
          </Button>
        }
      />
      <Card>
        {usuarios?.length > 0 ? (
          <Table columns={columns} data={usuarios} />
        ) : (
          <EmptyState
            title="Sin usuarios"
            actionLabel="Registrar"
            onAction={openCreate}
          />
        )}
      </Card>

      <Modal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title="Registrar Usuario"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Nombre"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              required
            />
            <Input
              label="Apellido"
              value={form.apellido}
              onChange={(e) => setForm({ ...form, apellido: e.target.value })}
              required
            />
          </div>
          <Input
            label="Email"
            type="email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
          <Input
            label="Contrasena"
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
            minLength={8}
          />
          <Input
            label="Telefono"
            value={form.telefono}
            onChange={(e) => setForm({ ...form, telefono: e.target.value })}
          />
          <Select
            label="Rol"
            value={form.rol}
            onChange={(e) => setForm({ ...form, rol: e.target.value })}
            options={rolOptions}
            required
          />
          <div className="flex justify-end gap-2 pt-4">
            <Button
              variant="secondary"
              type="button"
              onClick={() => setModalOpen(false)}
            >
              Cancelar
            </Button>
            <Button type="submit">Registrar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
