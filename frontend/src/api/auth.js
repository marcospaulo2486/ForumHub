import api from './axios';

export async function login(login, senha) {
  const { data } = await api.post('/login', { login, senha });
  return data;
}

export async function register(login, senha, nomeExibicao) {
  await api.post('/register', { login, senha, nomeExibicao });
}
