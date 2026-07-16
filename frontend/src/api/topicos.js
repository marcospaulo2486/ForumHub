import api from './axios';

export async function getTopicos() {
  const { data } = await api.get('/topicos');
  return data;
}

export async function getTopico(id) {
  const { data } = await api.get(`/topicos/${id}`);
  return data;
}

export async function createTopico({ titulo, mensagem, curso }) {
  const { data } = await api.post('/topicos', { titulo, mensagem, curso });
  return data;
}

export async function updateTopico(id, { titulo, mensagem, curso }) {
  const { data } = await api.put(`/topicos/${id}`, { titulo, mensagem, curso });
  return data;
}

export async function deleteTopico(id) {
  await api.delete(`/topicos/${id}`);
}
