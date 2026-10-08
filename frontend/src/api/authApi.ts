import { request } from './client'
import type { LoginResult } from '../types'

type Credentials = { email: string; password: string }

export const authApi = {
  login: (credentials: Credentials) => request<LoginResult>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }),
  register: (credentials: Credentials) => request<{ id: number; email: string }>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(credentials),
  }),
}
