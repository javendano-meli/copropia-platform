import client from './client';

const pqrService = {
  getFeed: (copropiedadId) => client.get(`/pqr/feed/${copropiedadId}`),
  getById: (id) => client.get(`/pqr/${id}`),
  getByUsuario: (usuarioId) => client.get(`/pqr/usuario/${usuarioId}`),
  getAllByCopropiedad: (copropiedadId) => client.get(`/pqr/copropiedad/${copropiedadId}`),
  create: (data) => client.post('/pqr', data),
  cambiarEstado: (id, estado) => client.patch(`/pqr/${id}/estado`, { estado }),
  getComentarios: (pqrId) => client.get(`/pqr-comentarios/pqr/${pqrId}`),
  createComentario: (data) => client.post('/pqr-comentarios', data),
};

export default pqrService;
