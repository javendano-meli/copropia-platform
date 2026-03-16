import client from './client';

const authService = {
  login: (email, password) => client.post('/auth/login', { email, password }),
  register: (data) => client.post('/auth/register', data),
  getUsuario: (id) => client.get(`/usuarios/${id}`),
  getUsuariosByCopropiedad: (copropiedadId) => client.get(`/usuarios/copropiedad/${copropiedadId}`),
  deactivateUsuario: (id) => client.delete(`/usuarios/${id}`),
};

export default authService;
