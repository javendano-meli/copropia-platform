import { useState, useEffect } from 'react';
import { MessageCircle, Image, Video, Lock, Globe, Send, ChevronDown, ChevronUp, AlertCircle, HelpCircle, Megaphone, Lightbulb } from 'lucide-react';
import toast from 'react-hot-toast';
import Header from '../../components/layout/Header';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Badge from '../../components/common/Badge';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';
import { useAuth } from '../../hooks/useAuth';
import pqrService from '../../api/pqrService';

const tipoIcons = {
  PETICION: HelpCircle,
  QUEJA: AlertCircle,
  RECLAMO: Megaphone,
  SUGERENCIA: Lightbulb,
};

const tipoColors = {
  PETICION: 'bg-blue-100 text-blue-700',
  QUEJA: 'bg-red-100 text-red-700',
  RECLAMO: 'bg-orange-100 text-orange-700',
  SUGERENCIA: 'bg-green-100 text-green-700',
};

const estadoOptions = [
  { value: 'ABIERTO', label: 'Abierto' },
  { value: 'EN_PROCESO', label: 'En Proceso' },
  { value: 'RESUELTO', label: 'Resuelto' },
  { value: 'CERRADO', label: 'Cerrado' },
];

function timeAgo(dateStr) {
  if (!dateStr) return '';
  const now = new Date();
  const date = new Date(dateStr);
  const diff = Math.floor((now - date) / 1000);
  if (diff < 60) return 'Hace un momento';
  if (diff < 3600) return `Hace ${Math.floor(diff / 60)} min`;
  if (diff < 86400) return `Hace ${Math.floor(diff / 3600)}h`;
  if (diff < 604800) return `Hace ${Math.floor(diff / 86400)}d`;
  return date.toLocaleDateString('es-CO', { day: 'numeric', month: 'short' });
}

function UserAvatar({ name }) {
  const initials = (name || 'U').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  return (
    <div className="w-10 h-10 rounded-full bg-primary-100 text-primary-700 flex items-center justify-center font-bold text-sm flex-shrink-0">
      {initials}
    </div>
  );
}

function PqrCard({ pqr, onComment, onEstadoChange, isAdmin }) {
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(false);
  const [newComment, setNewComment] = useState('');
  const TipoIcon = tipoIcons[pqr.tipo] || HelpCircle;

  const loadComments = async () => {
    if (!showComments) {
      setShowComments(true);
      setLoadingComments(true);
      try {
        const res = await pqrService.getComentarios(pqr.id);
        setComments(res.data || []);
      } catch { setComments([]); }
      finally { setLoadingComments(false); }
    } else {
      setShowComments(false);
    }
  };

  const handleComment = async () => {
    if (!newComment.trim()) return;
    try {
      await onComment(pqr.id, newComment);
      setNewComment('');
      const res = await pqrService.getComentarios(pqr.id);
      setComments(res.data || []);
    } catch (err) { toast.error(err?.message || 'Error al comentar'); }
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 mb-4">
      {/* Header */}
      <div className="px-5 pt-5 pb-3">
        <div className="flex items-start gap-3">
          <UserAvatar name={pqr.usuarioNombre} />
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 flex-wrap">
              <span className="font-semibold text-gray-900 text-sm">{pqr.usuarioNombre}</span>
              <span className="text-xs text-gray-400">{timeAgo(pqr.createdAt)}</span>
              {pqr.esPublico
                ? <Globe className="w-3.5 h-3.5 text-gray-400" title="Publico" />
                : <Lock className="w-3.5 h-3.5 text-warning-500" title="Privado" />
              }
            </div>
            <div className="flex items-center gap-2 mt-1">
              <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${tipoColors[pqr.tipo]}`}>
                <TipoIcon className="w-3 h-3" />
                {pqr.tipo}
              </span>
              <Badge text={pqr.estado} />
              {isAdmin && pqr.estado !== 'CERRADO' && (
                <select
                  className="text-xs border rounded px-1 py-0.5 text-gray-500 cursor-pointer"
                  value={pqr.estado}
                  onChange={(e) => onEstadoChange(pqr.id, e.target.value)}
                >
                  {estadoOptions.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              )}
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="mt-3">
          <h4 className="font-semibold text-gray-900">{pqr.titulo}</h4>
          <p className="text-sm text-gray-600 mt-1 whitespace-pre-wrap">{pqr.contenido}</p>
        </div>

        {/* Attachments */}
        {pqr.adjuntos && pqr.adjuntos.length > 0 && (
          <div className="mt-3 flex gap-2 flex-wrap">
            {pqr.adjuntos.map((adj, i) => (
              <div key={i} className="relative group">
                {adj.tipoArchivo === 'IMAGEN' ? (
                  <img src={adj.url} alt={adj.nombre} className="w-32 h-32 object-cover rounded-lg border border-gray-200" />
                ) : (
                  <div className="w-32 h-32 bg-gray-100 rounded-lg border border-gray-200 flex flex-col items-center justify-center">
                    <Video className="w-8 h-8 text-gray-400" />
                    <span className="text-xs text-gray-500 mt-1">{adj.nombre}</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Actions bar */}
      <div className="px-5 py-2.5 border-t border-gray-100 flex items-center gap-4">
        <button
          onClick={loadComments}
          className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-primary-600 transition-colors"
        >
          <MessageCircle className="w-4 h-4" />
          {pqr.cantidadComentarios || 0} Comentarios
          {showComments ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
        </button>
      </div>

      {/* Comments section */}
      {showComments && (
        <div className="px-5 pb-4 border-t border-gray-100">
          {loadingComments ? (
            <div className="py-3 text-center text-sm text-gray-400">Cargando comentarios...</div>
          ) : (
            <div className="space-y-3 mt-3">
              {comments.map((c) => (
                <div key={c.id} className="flex gap-2.5">
                  <UserAvatar name={c.usuarioNombre} />
                  <div className="flex-1 bg-gray-50 rounded-lg px-3 py-2">
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-medium text-gray-900">{c.usuarioNombre}</span>
                      <span className="text-xs text-gray-400">{timeAgo(c.createdAt)}</span>
                    </div>
                    <p className="text-sm text-gray-600 mt-0.5">{c.contenido}</p>
                  </div>
                </div>
              ))}
              {comments.length === 0 && (
                <p className="text-sm text-gray-400 text-center py-2">Sin comentarios aun. Se el primero!</p>
              )}
            </div>
          )}

          {/* New comment input */}
          {pqr.estado !== 'CERRADO' && pqr.esPublico && (
            <div className="flex gap-2 mt-3">
              <input
                type="text"
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleComment()}
                placeholder="Escribe un comentario..."
                className="flex-1 px-3 py-2 text-sm border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              />
              <button
                onClick={handleComment}
                disabled={!newComment.trim()}
                className="p-2 bg-primary-600 text-white rounded-full hover:bg-primary-700 disabled:opacity-50 transition-colors"
              >
                <Send className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default function PqrFeedPage() {
  const { user, isAdmin } = useAuth();
  const copId = user?.copropiedadId || 1;
  const [activeTab, setActiveTab] = useState('feed');
  const [pqrs, setPqrs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [form, setForm] = useState({
    tipo: 'PETICION', titulo: '', contenido: '', esPublico: true,
    adjuntos: [],
  });
  const [newAdjunto, setNewAdjunto] = useState({ url: '', tipoArchivo: 'IMAGEN', nombre: '' });

  const loadPqrs = async () => {
    setLoading(true);
    try {
      let res;
      if (activeTab === 'feed') res = await pqrService.getFeed(copId);
      else if (activeTab === 'mis') res = await pqrService.getByUsuario(1);
      else res = await pqrService.getAllByCopropiedad(copId);
      setPqrs(res.data || []);
    } catch { setPqrs([]); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadPqrs(); }, [activeTab, copId]);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await pqrService.create({
        copropiedadId: copId, usuarioId: 1, usuarioNombre: user?.email || 'Usuario',
        tipo: form.tipo, titulo: form.titulo, contenido: form.contenido,
        esPublico: form.esPublico, adjuntos: form.adjuntos,
      });
      toast.success('PQR publicada!');
      setForm({ tipo: 'PETICION', titulo: '', contenido: '', esPublico: true, adjuntos: [] });
      setShowCreateForm(false);
      loadPqrs();
    } catch (err) { toast.error(err?.message || 'Error al publicar'); }
  };

  const handleComment = async (pqrId, contenido) => {
    await pqrService.createComentario({
      pqrId, usuarioId: 1, usuarioNombre: user?.email || 'Usuario', contenido,
    });
    toast.success('Comentario agregado');
    loadPqrs();
  };

  const handleEstadoChange = async (id, estado) => {
    try {
      await pqrService.cambiarEstado(id, estado);
      toast.success('Estado actualizado');
      loadPqrs();
    } catch (err) { toast.error(err?.message || 'Error'); }
  };

  const addAdjunto = () => {
    if (!newAdjunto.url.trim()) return;
    setForm({ ...form, adjuntos: [...form.adjuntos, { ...newAdjunto }] });
    setNewAdjunto({ url: '', tipoArchivo: 'IMAGEN', nombre: '' });
  };

  const removeAdjunto = (i) => {
    setForm({ ...form, adjuntos: form.adjuntos.filter((_, idx) => idx !== i) });
  };

  const tabs = [
    { id: 'feed', label: 'Feed Publico', icon: Globe },
    { id: 'mis', label: 'Mis PQRs', icon: Lock },
  ];
  if (isAdmin()) tabs.push({ id: 'todas', label: 'Todas (Admin)', icon: Megaphone });

  const tipoOptions = [
    { value: 'PETICION', label: 'Peticion' },
    { value: 'QUEJA', label: 'Queja' },
    { value: 'RECLAMO', label: 'Reclamo' },
    { value: 'SUGERENCIA', label: 'Sugerencia' },
  ];

  return (
    <div>
      <Header title="PQR" subtitle="Peticiones, Quejas, Reclamos y Sugerencias" />

      {/* Tabs */}
      <div className="flex gap-1 bg-white rounded-xl shadow-sm border border-gray-200 p-1 mb-6">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-lg text-sm font-medium transition-colors flex-1 justify-center ${
              activeTab === tab.id ? 'bg-primary-600 text-white' : 'text-gray-500 hover:bg-gray-50'
            }`}
          >
            <tab.icon className="w-4 h-4" />
            {tab.label}
          </button>
        ))}
      </div>

      {/* Create Post */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 mb-6">
        {!showCreateForm ? (
          <button
            onClick={() => setShowCreateForm(true)}
            className="w-full px-5 py-4 text-left text-sm text-gray-400 hover:bg-gray-50 rounded-xl transition-colors"
          >
            Que esta pasando en tu copropiedad? Publica una peticion, queja o sugerencia...
          </button>
        ) : (
          <form onSubmit={handleCreate} className="p-5 space-y-4">
            <div className="flex gap-4">
              <Select label="Tipo" value={form.tipo} onChange={(e) => setForm({...form, tipo: e.target.value})} options={tipoOptions} className="w-48" />
              <Input label="Titulo" value={form.titulo} onChange={(e) => setForm({...form, titulo: e.target.value})} placeholder="Resumen de tu solicitud" className="flex-1" required />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Contenido</label>
              <textarea
                value={form.contenido}
                onChange={(e) => setForm({...form, contenido: e.target.value})}
                placeholder="Describe tu peticion, queja, reclamo o sugerencia en detalle..."
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 min-h-[100px] resize-y"
                required
              />
            </div>

            {/* Attachments section */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Adjuntos</label>
              {form.adjuntos.map((adj, i) => (
                <div key={i} className="flex items-center gap-2 mb-2 text-sm">
                  {adj.tipoArchivo === 'IMAGEN' ? <Image className="w-4 h-4 text-blue-500" /> : <Video className="w-4 h-4 text-purple-500" />}
                  <span className="text-gray-600 truncate flex-1">{adj.nombre || adj.url}</span>
                  <button type="button" onClick={() => removeAdjunto(i)} className="text-danger-500 text-xs hover:underline">Eliminar</button>
                </div>
              ))}
              <div className="flex gap-2 items-end">
                <Input placeholder="URL de la imagen o video" value={newAdjunto.url} onChange={(e) => setNewAdjunto({...newAdjunto, url: e.target.value})} className="flex-1" />
                <select value={newAdjunto.tipoArchivo} onChange={(e) => setNewAdjunto({...newAdjunto, tipoArchivo: e.target.value})} className="px-2 py-2 border border-gray-300 rounded-lg text-sm">
                  <option value="IMAGEN">Imagen</option>
                  <option value="VIDEO">Video</option>
                </select>
                <Input placeholder="Nombre" value={newAdjunto.nombre} onChange={(e) => setNewAdjunto({...newAdjunto, nombre: e.target.value})} className="w-32" />
                <Button type="button" variant="outline" size="sm" onClick={addAdjunto}>+</Button>
              </div>
            </div>

            <div className="flex items-center justify-between pt-2">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" checked={form.esPublico} onChange={(e) => setForm({...form, esPublico: e.target.checked})} className="rounded border-gray-300 text-primary-600" />
                <Globe className="w-4 h-4 text-gray-400" />
                <span className="text-sm text-gray-600">Publicar en el feed publico</span>
              </label>
              <div className="flex gap-2">
                <Button variant="secondary" type="button" onClick={() => setShowCreateForm(false)}>Cancelar</Button>
                <Button type="submit">Publicar</Button>
              </div>
            </div>
          </form>
        )}
      </div>

      {/* Feed */}
      {loading ? (
        <Loading message="Cargando feed..." />
      ) : pqrs.length > 0 ? (
        <div>
          {pqrs.map((pqr) => (
            <PqrCard
              key={pqr.id}
              pqr={pqr}
              onComment={handleComment}
              onEstadoChange={handleEstadoChange}
              isAdmin={isAdmin()}
            />
          ))}
        </div>
      ) : (
        <EmptyState
          title="Sin publicaciones"
          description={activeTab === 'feed' ? 'Se el primero en publicar en el feed de tu copropiedad' : 'No tienes PQRs registradas'}
          actionLabel="Crear PQR"
          onAction={() => setShowCreateForm(true)}
        />
      )}
    </div>
  );
}
