import { describe, it, expect, vi, beforeEach } from 'vitest';

describe('API Client', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('stores and retrieves token from localStorage', () => {
    localStorage.setItem('token', 'test-token');
    expect(localStorage.getItem('token')).toBe('test-token');
  });

  it('removes token on clear', () => {
    localStorage.setItem('token', 'test-token');
    localStorage.removeItem('token');
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('stores and retrieves user from localStorage', () => {
    const user = { email: 'test@test.com', rol: 'ADMIN' };
    localStorage.setItem('user', JSON.stringify(user));
    expect(JSON.parse(localStorage.getItem('user'))).toEqual(user);
  });
});
