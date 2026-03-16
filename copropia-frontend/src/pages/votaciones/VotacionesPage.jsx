import { useState, useEffect } from 'react';
import { Plus, Play, Square, BarChart3 } from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
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
import asambleaService from '../../api/asambleaService';
import votacionService from '../../api/votacionService';
import votoService from '../../api/votoService';

const COLORS = ['#22c55e', '#ef4444', '#f59e0b', '#3b82f6', '#8b5cf6', '#ec4899'];
const tipoOptions = [
  { value: 'APROBACION_SIMPLE', label: 'Aprobacion Simple' },
  { value: 'MAYORIA_CALIFICADA', label: 'Mayoria Calificada' },
  { value: 'ELECCION_MULTIPLE', label: 'Eleccion Multiple' },
];

export default function VotacionesPage() {
  const { user, isAdmin } = useAuth();
  const copId = user?.copropiedadId || 1;
  const { data: asambleas } = useFetch(() => asambleaService.getByCopropiedad(copId), [copId]);
  const [selectedAsamblea, setSelectedAsamblea] = useState('');
  const [votaciones, setVotaciones] = useState([]);
  const [loadingVotaciones, setLoadingVotaciones] = useState(false);
  const [modalCreate, setModalCreate] = useState(false);
  const [modalVotar, setModalVotar] = useState(false);
  const [modalResultados, setModalResultados] = useState(false);
  const [selectedVotacion, setSelectedVotacion] = useState(null);
  const [resultados, setResultados] = useState(null);
  const [form, setForm] = useState({ titulo: '', descripcion: '', tipoVotacion: 'APROBACION_SIMPLE', orden: 1, opciones: ['Si', 'No', 'Abstencion'] });
  const [votoForm, setVotoForm] = useState({ opcionId: '', propiedadId: '', coeficienteAplicado: '' });

  const loadVotaciones = async (asambleaId) => {
    if (!asambleaId) { setVotaciones([]); return; }
    setLoadingVotaciones(true);
    try {
      const res = await votacionService.getByAsamblea(asambleaId);
      setVotaciones(res.data || []);
    } catch { setVotaciones([]); }
    finally { setLoadingVotaciones(false); }
  };

  useEffect(() => { if (selectedAsamblea) loadVotaciones(selectedAsamblea); }, [selectedAsamblea]);

  const handleCreateVotacion = async (e) => {
    e.preventDefault();
    try {
      await votacionService.create({ asambleaId: Number(selectedAsamblea), titulo: form.titulo, descripcion: form.descripcion, tipoVotacion: form.tipoVotacion, orden: form.orden, opciones: form.opciones.filter(o => o.trim()) });
      toast.success('Votacion creada');
      setModalCreate(false);
      loadVotaciones(selectedAsamblea);
    } catch (err) { toast.error(err?.message || 'Error'); }
  };

  const handleOpen = async (id) => {
    try { await votacionService.open(id); toast.success('Votacion abierta'); loadVotaciones(selectedAsamblea); }
    catch (err) { toast.error(err?.message || 'Error'); }
  };

  const handleClose = async (id) => {
    try { await votacionService.close(id); toast.success('Votacion cerrada'); loadVotaciones(selectedAsamblea); }
    catch (err) { toast.error(err?.message || 'Error'); }
  };

  const openVotar = (votacion) => { setSelectedVotacion(votacion); setVotoForm({ opcionId: '', propiedadId: '', coeficienteAplicado: '' }); setModalVotar(true); };

  const handleVotar = async (e) => {
    e.preventDefault();
    try {
      await votoService.emitir({ votacionId: selectedVotacion.id, opcionId: Number(votoForm.opcionId), usuarioId: 1, propiedadId: Number(votoForm.propiedadId), coeficienteAplicado: Number(votoForm.coeficienteAplicado) });
      toast.success('Voto registrado!');
      setModalVotar(false);
    } catch (err) { toast.error(err?.message || 'Error al votar'); }
  };

  const viewResultados = async (votacion) => {
    try {
      const res = await votoService.getResultados(votacion.id, copId);
      setResultados(res.data);
      setSelectedVotacion(votacion);
      setModalResultados(true);
    } catch (err) { toast.error(err?.message || 'Error'); }
  };

  const addOpcion = () => setForm({ ...form, opciones: [...form.opciones, ''] });
  const updateOpcion = (i, val) => { const ops = [...form.opciones]; ops[i] = val; setForm({ ...form, opciones: ops }); };
  const removeOpcion = (i) => { const ops = form.opciones.filter((_, idx) => idx !== i); setForm({ ...form, opciones: ops }); };

  const columns = [
    { key: 'orden', label: '#', render: (row) => row.orden },
    { key: 'titulo', label: 'Titulo' },
    { key: 'tipoVotacion', label: 'Tipo', render: (row) => <Badge text={row.tipoVotacion} /> },
    { key: 'estado', label: 'Estado', render: (row) => <Badge text={row.estado} /> },
    { key: 'opciones', label: 'Opciones', render: (row) => row.opciones?.map(o => o.nombre).join(', ') || '-' },
    { key: 'actions', label: 'Acciones', render: (row) => (
      <div className="flex gap-1">
        {row.estado === 'PENDIENTE' && isAdmin() && (
          <button onClick={() => handleOpen(row.id)} className="p-1.5 hover:bg-success-50 rounded-lg" title="Abrir"><Play className="w-4 h-4 text-success-500" /></button>
        )}
        {row.estado === 'ABIERTA' && (
          <>
            <button onClick={() => openVotar(row)} className="p-1.5 hover:bg-primary-50 rounded-lg" title="Votar"><BarChart3 className="w-4 h-4 text-primary-600" /></button>
            {isAdmin() && <button onClick={() => handleClose(row.id)} className="p-1.5 hover:bg-danger-50 rounded-lg" title="Cerrar"><Square className="w-4 h-4 text-danger-500" /></button>}
          </>
        )}
        <button onClick={() => viewResultados(row)} className="p-1.5 hover:bg-gray-100 rounded-lg" title="Resultados"><BarChart3 className="w-4 h-4 text-gray-500" /></button>
      </div>
    )},
  ];

  return (
    <div>
      <Header title="Votaciones" subtitle="Votaciones ponderadas por coeficiente de propiedad" />

      <Card className="mb-6">
        <div className="flex items-end gap-4">
          <Select label="Seleccionar Asamblea" value={selectedAsamblea} onChange={(e) => setSelectedAsamblea(e.target.value)}
            options={(asambleas || []).map(a => ({ value: a.id, label: `${a.nombre} - ${a.estado}` }))} className="flex-1" />
          {selectedAsamblea && isAdmin() && (
            <Button onClick={() => { setForm({ titulo: '', descripcion: '', tipoVotacion: 'APROBACION_SIMPLE', orden: votaciones.length + 1, opciones: ['Si', 'No', 'Abstencion'] }); setModalCreate(true); }}>
              <Plus className="w-4 h-4" /> Nueva Votacion
            </Button>
          )}
        </div>
      </Card>

      {selectedAsamblea && (
        <Card>
          {loadingVotaciones ? <Loading /> : votaciones.length > 0 ? <Table columns={columns} data={votaciones} /> : <EmptyState title="Sin votaciones" description="Crea la primera votacion para esta asamblea" />}
        </Card>
      )}

      {/* Modal Crear Votacion */}
      <Modal isOpen={modalCreate} onClose={() => setModalCreate(false)} title="Nueva Votacion" size="lg">
        <form onSubmit={handleCreateVotacion} className="space-y-4">
          <Input label="Titulo" value={form.titulo} onChange={(e) => setForm({...form, titulo: e.target.value})} required />
          <Input label="Descripcion" value={form.descripcion} onChange={(e) => setForm({...form, descripcion: e.target.value})} />
          <div className="grid grid-cols-2 gap-4">
            <Select label="Tipo de Votacion" value={form.tipoVotacion} onChange={(e) => setForm({...form, tipoVotacion: e.target.value})} options={tipoOptions} required />
            <Input label="Orden" type="number" value={form.orden} onChange={(e) => setForm({...form, orden: Number(e.target.value)})} required />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Opciones de Voto</label>
            {form.opciones.map((op, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <Input value={op} onChange={(e) => updateOpcion(i, e.target.value)} placeholder={`Opcion ${i + 1}`} className="flex-1" required />
                {form.opciones.length > 2 && <Button variant="danger" size="sm" type="button" onClick={() => removeOpcion(i)}>X</Button>}
              </div>
            ))}
            <Button variant="outline" size="sm" type="button" onClick={addOpcion}>+ Agregar Opcion</Button>
          </div>
          <div className="flex justify-end gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setModalCreate(false)}>Cancelar</Button>
            <Button type="submit">Crear Votacion</Button>
          </div>
        </form>
      </Modal>

      {/* Modal Votar */}
      <Modal isOpen={modalVotar} onClose={() => setModalVotar(false)} title={`Votar: ${selectedVotacion?.titulo}`}>
        <form onSubmit={handleVotar} className="space-y-4">
          <Select label="Su Voto" value={votoForm.opcionId} onChange={(e) => setVotoForm({...votoForm, opcionId: e.target.value})}
            options={(selectedVotacion?.opciones || []).map(o => ({ value: o.id, label: o.nombre }))} required />
          <Input label="ID Propiedad" type="number" value={votoForm.propiedadId} onChange={(e) => setVotoForm({...votoForm, propiedadId: e.target.value})} required />
          <Input label="Coeficiente de su Propiedad (%)" type="number" step="0.0001" value={votoForm.coeficienteAplicado} onChange={(e) => setVotoForm({...votoForm, coeficienteAplicado: e.target.value})} required />
          <div className="flex justify-end gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setModalVotar(false)}>Cancelar</Button>
            <Button type="submit">Emitir Voto</Button>
          </div>
        </form>
      </Modal>

      {/* Modal Resultados */}
      <Modal isOpen={modalResultados} onClose={() => setModalResultados(false)} title={`Resultados: ${selectedVotacion?.titulo}`} size="lg">
        {resultados && (
          <div>
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="bg-primary-50 rounded-lg p-4 text-center">
                <p className="text-sm text-gray-500">Total Votos</p>
                <p className="text-2xl font-bold text-primary-700">{resultados.totalVotos}</p>
              </div>
              <div className="bg-success-50 rounded-lg p-4 text-center">
                <p className="text-sm text-gray-500">Coeficiente Votado</p>
                <p className="text-2xl font-bold text-success-700">{resultados.coeficienteTotal?.toFixed(4)}%</p>
              </div>
              <div className="bg-warning-50 rounded-lg p-4 text-center">
                <p className="text-sm text-gray-500">Participacion</p>
                <p className="text-2xl font-bold text-warning-700">{resultados.porcentajeParticipacion?.toFixed(2)}%</p>
              </div>
            </div>

            <div className="flex gap-8">
              <div className="flex-1">
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie data={(resultados.resultadosPorOpcion || []).map(r => ({ name: r.opcion, value: Number(r.coeficienteTotal) }))}
                      cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({ name, percent }) => `${name} ${(percent * 100).toFixed(1)}%`}>
                      {(resultados.resultadosPorOpcion || []).map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <div className="flex-1">
                <table className="w-full text-sm">
                  <thead><tr className="border-b"><th className="py-2 text-left">Opcion</th><th className="py-2 text-right">Votos</th><th className="py-2 text-right">Coeficiente</th><th className="py-2 text-right">%</th></tr></thead>
                  <tbody>
                    {(resultados.resultadosPorOpcion || []).map((r, i) => (
                      <tr key={i} className="border-b border-gray-50">
                        <td className="py-2 flex items-center gap-2"><span className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[i % COLORS.length] }} />{r.opcion}</td>
                        <td className="py-2 text-right">{r.cantidadVotos}</td>
                        <td className="py-2 text-right">{r.coeficienteTotal?.toFixed(4)}%</td>
                        <td className="py-2 text-right font-medium">{r.porcentaje?.toFixed(2)}%</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
