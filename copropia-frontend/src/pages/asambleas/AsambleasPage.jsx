import { useState } from 'react';
import { Plus, Play, Square, Eye } from 'lucide-react';
import toast from 'react-hot-toast';
import Header from '../../components/layout/Header';
import Card from '../../components/common/Card';
import Table from '../../components/common/Table';
import Button from '../../components/common/Button';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Badge from '../../components/common/Badge';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';
import { useAuth } from '../../hooks/useAuth';
import { useFetch } from '../../hooks/useFetch';
import asambleaService from '../../api/asambleaService';
import { formatDateTime } from '../../utils/formatters';

export default function AsambleasPage() {
  const { user, isAdmin } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: asambleas, loading, refetch } = useFetch(() => asambleaService.getByCopropiedad(copId), [copId]);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ nombre: '', descripcion: '', fechaProgramada: '', quorumRequerido: '51.00' });

  const openCreate = () => { setForm({ nombre: '', descripcion: '', fechaProgramada: '', quorumRequerido: '51.00' }); setModalOpen(true); };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await asambleaService.create({ copropiedadId: copId, creadoPor: 1, nombre: form.nombre, descripcion: form.descripcion, fechaProgramada: form.fechaProgramada, quorumRequerido: Number(form.quorumRequerido) });
      toast.success('Asamblea creada');
      setModalOpen(false);
      refetch();
    } catch (err) { toast.error(err?.message || 'Error al crear'); }
  };

  const handleOpen = async (id) => {
    try { await asambleaService.open(id); toast.success('Asamblea abierta'); refetch(); }
    catch (err) { toast.error(err?.message || 'Error'); }
  };

  const handleClose = async (id) => {
    if (!confirm('Cerrar esta asamblea? No se podra reabrir.')) return;
    try { await asambleaService.close(id); toast.success('Asamblea cerrada'); refetch(); }
    catch (err) { toast.error(err?.message || 'Error'); }
  };

  const columns = [
    { key: 'nombre', label: 'Nombre' },
    { key: 'fechaProgramada', label: 'Fecha', render: (row) => formatDateTime(row.fechaProgramada) },
    { key: 'quorumRequerido', label: 'Quorum', render: (row) => `${row.quorumRequerido}%` },
    { key: 'estado', label: 'Estado', render: (row) => <Badge text={row.estado} /> },
    { key: 'actions', label: 'Acciones', render: (row) => (
      <div className="flex gap-1">
        {row.estado === 'PROGRAMADA' && isAdmin() && (
          <button onClick={() => handleOpen(row.id)} className="p-1.5 hover:bg-success-50 rounded-lg" title="Abrir"><Play className="w-4 h-4 text-success-500" /></button>
        )}
        {row.estado === 'ABIERTA' && isAdmin() && (
          <button onClick={() => handleClose(row.id)} className="p-1.5 hover:bg-danger-50 rounded-lg" title="Cerrar"><Square className="w-4 h-4 text-danger-500" /></button>
        )}
      </div>
    )},
  ];

  if (loading) return <Loading />;

  return (
    <div>
      <Header title="Asambleas" subtitle="Gestion de asambleas de copropietarios" actions={isAdmin() && <Button onClick={openCreate}><Plus className="w-4 h-4" /> Nueva Asamblea</Button>} />
      <Card>
        {asambleas?.length > 0 ? <Table columns={columns} data={asambleas} /> : <EmptyState title="Sin asambleas" actionLabel={isAdmin() ? "Crear" : undefined} onAction={isAdmin() ? openCreate : undefined} />}
      </Card>

      <Modal isOpen={modalOpen} onClose={() => setModalOpen(false)} title="Nueva Asamblea">
        <form onSubmit={handleSubmit} className="space-y-4">
          <Input label="Nombre" value={form.nombre} onChange={(e) => setForm({...form, nombre: e.target.value})} placeholder="Asamblea Ordinaria 2026" required />
          <Input label="Descripcion" value={form.descripcion} onChange={(e) => setForm({...form, descripcion: e.target.value})} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Fecha y Hora" type="datetime-local" value={form.fechaProgramada} onChange={(e) => setForm({...form, fechaProgramada: e.target.value})} required />
            <Input label="Quorum Requerido (%)" type="number" step="0.01" value={form.quorumRequerido} onChange={(e) => setForm({...form, quorumRequerido: e.target.value})} required />
          </div>
          <div className="flex justify-end gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setModalOpen(false)}>Cancelar</Button>
            <Button type="submit">Crear Asamblea</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
