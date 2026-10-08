import { useEffect, useState } from 'react'
import { pokemonApi } from '../api/pokemonApi'
import type { ApiError, PokemonDetails, SyncResult } from '../types'
import EditPokemonPage from './EditPokemonPage'

type Props = { id: number; canManage: boolean; onBack: () => void; onSync?: (pokeApiId: number) => Promise<SyncResult> }

function PokemonDetailsPage({ id, canManage, onBack, onSync }: Props) {
  const [pokemon, setPokemon] = useState<PokemonDetails | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [syncing, setSyncing] = useState(false)
  const [syncMessage, setSyncMessage] = useState<string | null>(null)

  useEffect(() => {
    let active = true
    void pokemonApi.get(id)
      .then((result) => { if (active) setPokemon(result) })
      .catch((caught: ApiError) => { if (active) setError(caught.message ?? 'Unable to load details.') })
    return () => { active = false }
  }, [id])

  if (error) return <section><button onClick={onBack} type="button">Back to catalog</button><p className="status error">{error}</p></section>
  if (!pokemon) return <p className="status">Loading details...</p>

  async function syncCurrentPokemon() {
    if (!onSync) return
    const currentPokemon = pokemon
    if (!currentPokemon) return
    setSyncing(true)
    setError(null)
    setSyncMessage(null)
    try {
      const result = await onSync(currentPokemon.pokeApiId)
      setPokemon(await pokemonApi.get(id))
      setSyncMessage(`Sync complete: ${result.inserted} inserted, ${result.updated} updated.`)
    } catch (caught) {
      setError((caught as ApiError).message ?? 'Unable to synchronize this Pokemon.')
    } finally {
      setSyncing(false)
    }
  }

  return (
    <section>
      <button className="back-button" onClick={onBack} type="button">← Back to catalog</button>
      <div className="detail-layout">
        <div className="detail-main">
          <div className="detail-hero">
            {pokemon.spriteUrl ? <img alt={`${pokemon.name} sprite`} src={pokemon.spriteUrl} /> : null}
            <div><p className="eyebrow">#{pokemon.pokeApiId.toString().padStart(3, '0')}</p><h1>{pokemon.name}</h1><div className="tag-row">{pokemon.types.map((type) => <span className="tag" key={type}>{type}</span>)}</div></div>
          </div>
          <p>{pokemon.description ?? 'No narrative description available.'}</p>
          <div className="info-grid"><div><span className="eyebrow">WEIGHT</span><strong>{pokemon.weight} hg</strong></div><div><span className="eyebrow">ABILITIES</span><strong>{pokemon.abilities.join(', ') || 'Unknown'}</strong></div></div>
          <h2>Core statistics</h2>
          <div className="stats-list">{Object.entries(pokemon.stats).map(([stat, value]) => <div className="stat-row" key={stat}><span>{stat}</span><progress max="255" value={value} /><strong>{value}</strong></div>)}</div>
          <h2>Evolution line</h2><div className="evolution-line">{pokemon.evolutionChain.length ? pokemon.evolutionChain.join(' → ') : 'Unknown'}</div>
        </div>
        {canManage ? <div className="local-actions">
          {onSync ? <div className="sync-panel">
            <p>Synchronize this Pokemon before editing local fields.</p>
            <button disabled={syncing} onClick={() => void syncCurrentPokemon()} type="button">{syncing ? 'Syncing...' : 'Sync this Pokemon'}</button>
          </div> : null}
          {syncMessage ? <p className="status success" role="status">{syncMessage}</p> : null}
          <EditPokemonPage onSaved={setPokemon} pokemon={pokemon} />
        </div> : null}
      </div>
    </section>
  )
}

export default PokemonDetailsPage
