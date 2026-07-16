import api from './axios';

export async function login(login, senha) {
  const { data } = await api.post('/login', { login, senha });
  return data;
}

export async function register(login, senha) {
  await api.post('/register', { login, senha });
}
