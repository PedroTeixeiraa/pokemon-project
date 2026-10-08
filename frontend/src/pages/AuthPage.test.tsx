import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import AuthPage from './AuthPage'
import { authApi } from '../api/authApi'

vi.mock('../api/authApi', () => ({
  authApi: { login: vi.fn(), register: vi.fn() },
}))

describe('AuthPage', () => {
  it('submits login credentials', async () => {
    vi.mocked(authApi.login).mockResolvedValue({ token: 'token', user: { id: 1, email: 'user@example.com' } })
    const onAuthenticated = vi.fn()
    render(<AuthPage onAuthenticated={onAuthenticated} />)

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'user@example.com' } })
    fireEvent.change(screen.getByLabelText('Password'), { target: { value: 'secret123' } })
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    await waitFor(() => expect(onAuthenticated).toHaveBeenCalledWith({ token: 'token', user: { id: 1, email: 'user@example.com' } }))
  })
})
