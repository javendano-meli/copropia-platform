import client from './client';

const propiedadService = {
  getById: (id) => client.get(`/propiedades/${id}`),
  getByCopropiedad: (copropiedadId) => client.get(`/propiedades/copropiedad/${copropiedadId}`),
  getByPropietario: (propietarioId) => client.get(`/propiedades/propietario/${propietarioId}`),
  create: (data) => client.post('/propiedades', data),
  update: (id, data) => client.put(`/propiedades/${id}`, data),
  getTotalCoeficiente: (copropiedadId) => client.get(`/propiedades/copropiedad/${copropiedadId}/coeficiente-total`),
};

export default propiedadService;
