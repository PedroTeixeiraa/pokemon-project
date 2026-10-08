import { useState, type FormEvent } from 'react'
import { pokemonApi } from '../api/pokemonApi'
import type { ApiError, PokemonDetails } from '../types'

type Props = { pokemon: PokemonDetails; onSaved: (pokemon: PokemonDetails) => void }

function EditPokemonPage({ pokemon, onSaved }: Props) {
  const [localizedName, setLocalizedName] = useState(pokemon.localizedName ?? '')
  const [region, setRegion] = useState(pokemon.region ?? '')
  const [internalTags, setInternalTags] = useState(pokemon.internalTags.join(', '))
  const [notes, setNotes] = useState(pokemon.notes ?? '')
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    try {
      const saved = await pokemonApi.update(pokemon.id, {
        localizedName,
        region,
        internalTags: internalTags.split(',').map((tag) => tag.trim()).filter(Boolean),
        notes,
      })
      onSaved(saved)
    } catch (caught) {
      setError((caught as ApiError).message ?? 'Unable to save changes.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="edit-form" onSubmit={submit}>
      <h2>Local classification</h2>
      <label htmlFor="localizedName">Portuguese name</label>
      <input id="localizedName" onChange={(event) => setLocalizedName(event.target.value)} value={localizedName} />
      <label htmlFor="region">Region</label>
      <input id="region" onChange={(event) => setRegion(event.target.value)} value={region} />
      <label htmlFor="internalTags">Internal tags</label>
      <input id="internalTags" onChange={(event) => setInternalTags(event.target.value)} value={internalTags} />
      <label htmlFor="notes">Notes</label>
      <textarea id="notes" onChange={(event) => setNotes(event.target.value)} value={notes} />
      {error ? <p className="status error">{error}</p> : null}
      <button disabled={saving} type="submit">{saving ? 'Saving...' : 'Save local fields'}</button>
    </form>
  )
}

export default EditPokemonPage
