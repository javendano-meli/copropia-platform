import { createContext, useState, useEffect } from 'react';
import authService from '../api/authService';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(false);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await authService.login(email, password);
      const token = response.data.token;
      localStorage.setItem('token', token);

      const payload = JSON.parse(atob(token.split('.')[1]));
      const userData = {
        email: payload.sub,
        rol: payload.rol,
        copropiedadId: payload.copropiedadId,
      };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return userData;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = () => user?.rol === 'SUPER_ADMIN' || user?.rol === 'ADMIN_COPROPIEDAD';
  const isSuperAdmin = () => user?.rol === 'SUPER_ADMIN';

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, isAdmin, isSuperAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}
