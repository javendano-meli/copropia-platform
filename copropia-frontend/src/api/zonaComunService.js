import client from './client';

const zonaComunService = {
  getById: (id) => client.get(`/zonas-comunes/${id}`),
  getByCopropiedad: (copropiedadId) => client.get(`/zonas-comunes/copropiedad/${copropiedadId}`),
  create: (data) => client.post('/zonas-comunes', data),
  update: (id, data) => client.put(`/zonas-comunes/${id}`, data),
  deactivate: (id) => client.delete(`/zonas-comunes/${id}`),
};

export default zonaComunService;
