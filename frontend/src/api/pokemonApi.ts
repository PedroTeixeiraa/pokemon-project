import { request } from './client'
import type { PokemonDetails, PokemonPage, SyncResult, UpdatePokemonInput } from '../types'

export const pokemonApi = {
  list: (page = 0, size = 20) => request<PokemonPage>(`/api/pokemons?page=${page}&size=${size}`),
  get: (id: number) => request<PokemonDetails>(`/api/pokemons/${id}`),
  sync: (pokeApiIds: number[]) => request<SyncResult>('/api/pokemons/sync', {
    method: 'POST',
    body: JSON.stringify({ pokeApiIds }),
  }),
  update: (id: number, input: UpdatePokemonInput) => request<PokemonDetails>(`/api/pokemons/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(input),
  }),
}
