import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import PokemonCard from './PokemonCard'
import type { PokemonSummary } from '../types'

const pokemon: PokemonSummary = {
  id: 1,
  pokeApiId: 25,
  name: 'Pikachu',
  spriteUrl: 'sprite.png',
  types: ['electric'],
  weight: 60,
  abilities: ['static'],
}

describe('PokemonCard', () => {
  it('renders the essential Pokemon information', () => {
    render(<PokemonCard pokemon={pokemon} onSelect={() => undefined} />)

    expect(screen.getByRole('heading', { name: 'Pikachu' })).toBeInTheDocument()
    expect(screen.getByText('electric')).toBeInTheDocument()
    expect(screen.getByText('60 hg')).toBeInTheDocument()
  })
})
