import { useState, type FormEvent } from 'react'
import { authApi } from '../api/authApi'
import type { ApiError, LoginResult } from '../types'

type Props = { onAuthenticated: (result: LoginResult) => void }

function AuthPage({ onAuthenticated }: Props) {
  const [registering, setRegistering] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setLoading(true)
    setError(null)
    try {
      if (registering) {
        await authApi.register({ email, password })
      }
      const result = await authApi.login({ email, password })
      onAuthenticated(result)
    } catch (caught) {
      setError((caught as ApiError).message ?? 'Authentication failed.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="auth-card">
      <p className="eyebrow">SECURE ACCESS</p>
      <h1>{registering ? 'Create account' : 'Welcome back'}</h1>
      <form onSubmit={submit}>
        <label htmlFor="email">Email</label>
        <input id="email" onChange={(event) => setEmail(event.target.value)} required type="email" value={email} />
        <label htmlFor="password">Password</label>
        <input id="password" minLength={8} onChange={(event) => setPassword(event.target.value)} required type="password" value={password} />
        {error ? <p className="status error">{error}</p> : null}
        <button disabled={loading} type="submit">{loading ? 'Working...' : registering ? 'Create account' : 'Sign in'}</button>
      </form>
      <button className="link-button" onClick={() => setRegistering((current) => !current)} type="button">
        {registering ? 'Already have an account? Sign in' : 'Need an account? Register'}
      </button>
    </section>
  )
}

export default AuthPage
