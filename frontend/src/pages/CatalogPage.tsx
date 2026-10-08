import { useCallback, useEffect, useState } from 'react'
import PokemonCard from '../components/PokemonCard'
import Pagination from '../components/Pagination'
import { pokemonApi } from '../api/pokemonApi'
import type { ApiError, PokemonPage, SyncResult } from '../types'

type Props = { onSelect: (id: number) => void; canManage: boolean; onSync?: () => Promise<SyncResult> }

function CatalogPage({ onSelect, canManage, onSync }: Props) {
  const [result, setResult] = useState<PokemonPage | null>(null)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [syncing, setSyncing] = useState(false)
  const [syncMessage, setSyncMessage] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setResult(await pokemonApi.list(page))
    } catch (caught) {
      setError((caught as ApiError).message ?? 'Unable to load the catalog.')
    } finally {
      setLoading(false)
    }
  }, [page])

  useEffect(() => { void load() }, [load])

  async function sync() {
    if (!onSync) return
    setSyncing(true)
    setError(null)
    setSyncMessage(null)
    try {
      const result = await onSync()
      await load()
      setSyncMessage(`Sync complete: ${result.inserted} inserted, ${result.updated} updated.`)
    } catch (caught) {
      setError((caught as ApiError).message ?? 'Unable to sync the demo Pokemon.')
    } finally {
      setSyncing(false)
    }
  }

  return (
    <section>
      <div className="section-heading">
        <div><p className="eyebrow">LIVE POKEAPI CATALOG</p><h1>Pokemon catalog</h1></div>
        {canManage && onSync ? <button disabled={syncing} onClick={() => void sync()} type="button">{syncing ? 'Syncing...' : 'Sync demo Pokemon'}</button> : null}
      </div>
      {syncMessage ? <p className="status success" role="status">{syncMessage}</p> : null}
      {loading ? <p className="status">{result ? 'Loading page...' : 'Loading catalog...'}</p> : null}
      {error ? <p className="status error">{error}</p> : null}
      {!loading && !error && result?.content.length === 0 ? <p className="status">No Pokemon found. Sync some data to get started.</p> : null}
      <div className="pokemon-grid">{result?.content.map((pokemon) => <PokemonCard key={pokemon.id} pokemon={pokemon} onSelect={onSelect} />)}</div>
      {result ? <Pagination page={result.page} totalPages={result.totalPages} loading={loading} onChange={setPage} /> : null}
    </section>
  )
}

export default CatalogPage
