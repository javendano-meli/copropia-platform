import { render, screen, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { AuthProvider, AuthContext } from './AuthContext';
import { useContext } from 'react';

function TestConsumer() {
  const { user, logout, isAdmin, isSuperAdmin } = useContext(AuthContext);
  return (
    <div>
      <span data-testid="email">{user?.email || 'none'}</span>
      <span data-testid="is-admin">{String(isAdmin())}</span>
      <span data-testid="is-super">{String(isSuperAdmin())}</span>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('provides null user when no stored data', () => {
    render(<AuthProvider><TestConsumer /></AuthProvider>);
    expect(screen.getByTestId('email')).toHaveTextContent('none');
  });

  it('restores user from localStorage', () => {
    localStorage.setItem('user', JSON.stringify({ email: 'admin@test.com', rol: 'SUPER_ADMIN', copropiedadId: 1 }));
    render(<AuthProvider><TestConsumer /></AuthProvider>);
    expect(screen.getByTestId('email')).toHaveTextContent('admin@test.com');
  });

  it('identifies super admin', () => {
    localStorage.setItem('user', JSON.stringify({ email: 'a@t.com', rol: 'SUPER_ADMIN' }));
    render(<AuthProvider><TestConsumer /></AuthProvider>);
    expect(screen.getByTestId('is-super')).toHaveTextContent('true');
    expect(screen.getByTestId('is-admin')).toHaveTextContent('true');
  });

  it('identifies admin copropiedad', () => {
    localStorage.setItem('user', JSON.stringify({ email: 'a@t.com', rol: 'ADMIN_COPROPIEDAD' }));
    render(<AuthProvider><TestConsumer /></AuthProvider>);
    expect(screen.getByTestId('is-admin')).toHaveTextContent('true');
    expect(screen.getByTestId('is-super')).toHaveTextContent('false');
  });

  it('identifies propietario as not admin', () => {
    localStorage.setItem('user', JSON.stringify({ email: 'a@t.com', rol: 'PROPIETARIO' }));
    render(<AuthProvider><TestConsumer /></AuthProvider>);
    expect(screen.getByTestId('is-admin')).toHaveTextContent('false');
  });

  it('clears user on logout', async () => {
    localStorage.setItem('user', JSON.stringify({ email: 'a@t.com', rol: 'SUPER_ADMIN' }));
    localStorage.setItem('token', 'some-token');
    render(<AuthProvider><TestConsumer /></AuthProvider>);

    await userEvent.click(screen.getByText('Logout'));

    expect(screen.getByTestId('email')).toHaveTextContent('none');
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
