import client from './client';

const copropiedadService = {
  getAll: () => client.get('/copropiedades'),
  getById: (id) => client.get(`/copropiedades/${id}`),
  create: (data) => client.post('/copropiedades', data),
  update: (id, data) => client.put(`/copropiedades/${id}`, data),
  deactivate: (id) => client.delete(`/copropiedades/${id}`),
};

export default copropiedadService;
