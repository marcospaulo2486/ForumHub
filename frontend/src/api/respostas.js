import api from './axios';

export async function getRespostas(topicoId) {
  const { data } = await api.get(`/topicos/${topicoId}/respostas`);
  return data;
}

export async function createResposta(topicoId, { mensagem }) {
  const { data } = await api.post(`/topicos/${topicoId}/respostas`, { mensagem });
  return data;
}
