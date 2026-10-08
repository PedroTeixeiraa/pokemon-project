import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import PokemonDetailsPage from './PokemonDetailsPage'
import { pokemonApi } from '../api/pokemonApi'

vi.mock('../api/pokemonApi', () => ({
  pokemonApi: { get: vi.fn(), sync: vi.fn() },
}))

const fearow = {
  id: 22,
  pokeApiId: 22,
  name: 'fearow',
  spriteUrl: null,
  types: ['normal', 'flying'],
  weight: 380,
  abilities: ['keen-eye'],
  stats: { hp: 65 },
  description: 'A description',
  evolutionChain: ['spearow', 'fearow'],
  localizedName: null,
  region: null,
  internalTags: [],
  notes: null,
}

describe('PokemonDetailsPage', () => {
  it('syncs the Pokemon currently being viewed', async () => {
    vi.mocked(pokemonApi.get).mockResolvedValue(fearow)
    const onSync = vi.fn().mockResolvedValue({ inserted: 1, updated: 0 })

    render(<PokemonDetailsPage canManage id={22} onBack={() => undefined} onSync={onSync} />)

    fireEvent.click(await screen.findByRole('button', { name: 'Sync this Pokemon' }))

    await waitFor(() => expect(onSync).toHaveBeenCalledWith(22))
    expect(await screen.findByText('Sync complete: 1 inserted, 0 updated.')).toBeInTheDocument()
  })
})
