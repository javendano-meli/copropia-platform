import client from './client';

const reservaService = {
  getById: (id) => client.get(`/reservas/${id}`),
  getByZonaAndFecha: (zonaComunId, fecha) => client.get(`/reservas/zona/${zonaComunId}?fecha=${fecha}`),
  getByUsuario: (usuarioId) => client.get(`/reservas/usuario/${usuarioId}`),
  create: (data) => client.post('/reservas', data),
  confirm: (id) => client.patch(`/reservas/${id}/confirmar`),
  cancel: (id, usuarioId) => client.patch(`/reservas/${id}/cancelar?usuarioId=${usuarioId}`),
};

export default reservaService;
