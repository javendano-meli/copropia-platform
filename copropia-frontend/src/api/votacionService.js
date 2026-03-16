import client from './client';

const votacionService = {
  getById: (id) => client.get(`/votaciones/${id}`),
  getByAsamblea: (asambleaId) => client.get(`/votaciones/asamblea/${asambleaId}`),
  create: (data) => client.post('/votaciones', data),
  open: (id) => client.patch(`/votaciones/${id}/abrir`),
  close: (id) => client.patch(`/votaciones/${id}/cerrar`),
};

export default votacionService;
