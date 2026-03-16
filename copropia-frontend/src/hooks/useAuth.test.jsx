import { renderHook } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { AuthContext } from '../contexts/AuthContext';
import { useAuth } from './useAuth';

describe('useAuth', () => {
  it('returns context value', () => {
    const mockValue = {
      user: { email: 'test@test.com', rol: 'ADMIN_COPROPIEDAD' },
      login: () => {},
      logout: () => {},
      loading: false,
      isAdmin: () => true,
      isSuperAdmin: () => false,
    };

    const wrapper = ({ children }) => (
      <AuthContext.Provider value={mockValue}>{children}</AuthContext.Provider>
    );

    const { result } = renderHook(() => useAuth(), { wrapper });

    expect(result.current.user.email).toBe('test@test.com');
    expect(result.current.isAdmin()).toBe(true);
    expect(result.current.isSuperAdmin()).toBe(false);
  });

  it('throws error when used outside AuthProvider', () => {
    expect(() => {
      renderHook(() => useAuth());
    }).toThrow('useAuth must be used within an AuthProvider');
  });
});
