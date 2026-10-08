import { createContext, useContext, useMemo, useState, type ReactNode } from 'react'
import { clearStoredAuth, getStoredAuth, setStoredAuth } from '../api/client'
import type { LoginResult } from '../types'

type AuthContextValue = {
  auth: LoginResult | null
  login: (result: LoginResult) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<LoginResult | null>(() => getStoredAuth<LoginResult>())
  const value = useMemo(() => ({
    auth,
    login: (result: LoginResult) => { setStoredAuth(result); setAuth(result) },
    logout: () => { clearStoredAuth(); setAuth(null) },
  }), [auth])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const value = useContext(AuthContext)
  if (!value) throw new Error('useAuth must be used inside AuthProvider')
  return value
}
