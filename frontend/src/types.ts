export type PokemonSummary = {
  id: number
  pokeApiId: number
  name: string
  spriteUrl: string | null
  types: string[]
  weight: number
  abilities: string[]
}

export type PokemonDetails = PokemonSummary & {
  stats: Record<string, number>
  description: string | null
  evolutionChain: string[]
  localizedName: string | null
  region: string | null
  internalTags: string[]
  notes: string | null
}

export type PokemonPage = {
  content: PokemonSummary[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type LoginResult = {
  token: string
  user: { id: number; email: string }
}

export type SyncResult = { inserted: number; updated: number }

export type UpdatePokemonInput = {
  localizedName?: string
  region?: string
  internalTags?: string[]
  notes?: string
}

export type ApiError = { status: number; message: string; fields?: Record<string, string> }
