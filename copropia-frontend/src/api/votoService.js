import client from './client';

const votoService = {
  emitir: (data) => client.post('/votos', data),
  getResultados: (votacionId, copropiedadId) => client.get(`/votos/resultados/${votacionId}?copropiedadId=${copropiedadId}`),
};

export default votoService;
