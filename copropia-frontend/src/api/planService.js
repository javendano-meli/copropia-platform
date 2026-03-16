import client from './client';

const planService = {
  getAll: () => client.get('/planes'),
  getById: (id) => client.get(`/planes/${id}`),
  create: (data) => client.post('/planes', data),
  update: (id, data) => client.put(`/planes/${id}`, data),
};

export default planService;
