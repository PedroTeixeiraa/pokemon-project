import type { PokemonSummary } from '../types'

type Props = { pokemon: PokemonSummary; onSelect: (id: number) => void }

function PokemonCard({ pokemon, onSelect }: Props) {
  return (
    <button className="pokemon-card" onClick={() => onSelect(pokemon.id)} type="button">
      {pokemon.spriteUrl ? <img alt={`${pokemon.name} sprite`} src={pokemon.spriteUrl} /> : <div className="sprite-placeholder">?</div>}
      <div>
        <p className="eyebrow">#{pokemon.pokeApiId.toString().padStart(3, '0')}</p>
        <h2>{pokemon.name}</h2>
        <div className="tag-row">{pokemon.types.map((type) => <span className="tag" key={type}>{type}</span>)}</div>
        <p className="muted">{pokemon.weight} hg</p>
      </div>
    </button>
  )
}

export default PokemonCard
