import client from './client';

const asambleaService = {
  getById: (id) => client.get(`/asambleas/${id}`),
  getByCopropiedad: (copropiedadId) => client.get(`/asambleas/copropiedad/${copropiedadId}`),
  create: (data) => client.post('/asambleas', data),
  open: (id) => client.patch(`/asambleas/${id}/abrir`),
  close: (id) => client.patch(`/asambleas/${id}/cerrar`),
};

export default asambleaService;
