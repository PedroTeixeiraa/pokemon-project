import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import CatalogPage from './CatalogPage'
import { pokemonApi } from '../api/pokemonApi'

vi.mock('../api/pokemonApi', () => ({
  pokemonApi: { list: vi.fn(), sync: vi.fn() },
}))

describe('CatalogPage', () => {
  it('loads and renders a paginated catalog', async () => {
    vi.mocked(pokemonApi.list).mockResolvedValue({
      content: [{ id: 1, pokeApiId: 25, name: 'Pikachu', spriteUrl: null, types: ['electric'], weight: 60, abilities: ['static'] }],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
    })

    render(<CatalogPage onSelect={() => undefined} canManage={false} />)

    await waitFor(() => expect(screen.getByRole('heading', { name: 'Pokemon catalog' })).toBeInTheDocument())
    expect(screen.getByRole('heading', { name: 'Pikachu' })).toBeInTheDocument()
  })

  it('shows loading state and disables pagination while changing page', async () => {
    let resolveNextPage: ((value: never) => void) | undefined
    vi.mocked(pokemonApi.list)
      .mockResolvedValueOnce({ content: [], page: 0, size: 20, totalElements: 40, totalPages: 2 })
      .mockImplementationOnce(() => new Promise((resolve) => { resolveNextPage = resolve }))

    render(<CatalogPage onSelect={() => undefined} canManage={false} />)

    await waitFor(() => expect(screen.getByRole('button', { name: 'Next' })).toBeEnabled())
    fireEvent.click(screen.getByRole('button', { name: 'Next' }))

    expect(screen.getByText('Loading page...')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Previous' })).toBeDisabled()
    expect(screen.getByRole('button', { name: 'Next' })).toBeDisabled()

    resolveNextPage?.({ content: [], page: 1, size: 20, totalElements: 40, totalPages: 2 } as never)
    await waitFor(() => expect(screen.getByText('Page 2 of 2')).toBeInTheDocument())
  })

  it('labels the fixed demonstration synchronization action accurately', async () => {
    vi.mocked(pokemonApi.list).mockResolvedValue({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 })

    render(<CatalogPage onSelect={() => undefined} canManage onSync={vi.fn()} />)

    expect(await screen.findByRole('button', { name: 'Sync demo Pokemon' })).toBeInTheDocument()
  })

  it('shows the synchronization result after the demo Pokemon are synced', async () => {
    const onSync = vi.fn().mockResolvedValue({ inserted: 0, updated: 4 })
    vi.mocked(pokemonApi.list).mockResolvedValue({ content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 })

    render(<CatalogPage onSelect={() => undefined} canManage onSync={onSync} />)
    fireEvent.click(await screen.findByRole('button', { name: 'Sync demo Pokemon' }))

    expect(await screen.findByText('Sync complete: 0 inserted, 4 updated.')).toBeInTheDocument()
  })
})
