import { useState } from 'react'
import { pokemonApi } from './api/pokemonApi'
import { AuthProvider, useAuth } from './auth/AuthContext'
import AuthPage from './pages/AuthPage'
import CatalogPage from './pages/CatalogPage'
import PokemonDetailsPage from './pages/PokemonDetailsPage'

function Shell() {
  const { auth, login, logout } = useAuth()
  const [screen, setScreen] = useState<'catalog' | 'auth' | 'details'>('catalog')
  const [selectedId, setSelectedId] = useState<number | null>(null)

  async function syncDemoPokemon() {
    return pokemonApi.sync([1, 4, 7, 25])
  }

  return (
    <main>
      <header className="app-header"><button className="brand" onClick={() => setScreen('catalog')} type="button">P<span>●</span>KEDEX</button><div className="header-actions">{auth ? <><span className="user-pill">{auth.user.email}</span><button className="secondary-button" onClick={logout} type="button">Sign out</button></> : <button onClick={() => setScreen('auth')} type="button">Sign in</button>}</div></header>
      {screen === 'auth' ? <AuthPage onAuthenticated={(result) => { login(result); setScreen('catalog') }} /> : null}
      {screen === 'catalog' ? <CatalogPage canManage={Boolean(auth)} onSelect={(id) => { setSelectedId(id); setScreen('details') }} onSync={auth ? syncDemoPokemon : undefined} /> : null}
      {screen === 'details' && selectedId ? <PokemonDetailsPage canManage={Boolean(auth)} id={selectedId} onBack={() => setScreen('catalog')} onSync={auth ? (pokeApiId) => pokemonApi.sync([pokeApiId]) : undefined} /> : null}
    </main>
  )
}

function App() {
  return <AuthProvider><Shell /></AuthProvider>
}

export default App
