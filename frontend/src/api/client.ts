import type { ApiError } from '../types'

const authStorageKey = 'pokemon.auth.v1'

export function getStoredAuth<T>(): T | null {
  const raw = localStorage.getItem(authStorageKey)
  return raw ? (JSON.parse(raw) as T) : null
}

export function setStoredAuth(value: unknown): void {
  localStorage.setItem(authStorageKey, JSON.stringify(value))
}

export function clearStoredAuth(): void {
  localStorage.removeItem(authStorageKey)
}

export async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const stored = getStoredAuth<{ token: string }>()
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  if (stored?.token) headers.set('Authorization', `Bearer ${stored.token}`)

  const response = await fetch(`${import.meta.env.VITE_API_URL ?? 'http://localhost:8080'}${path}`, {
    ...options,
    headers,
  })
  if (!response.ok) {
    const error = (await response.json().catch(() => ({ status: response.status, message: response.statusText }))) as ApiError
    throw error
  }
  return response.status === 204 ? (undefined as T) : ((await response.json()) as T)
}
