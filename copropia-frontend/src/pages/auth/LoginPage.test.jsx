import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import { BrowserRouter } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import LoginPage from './LoginPage';

const mockLogin = vi.fn();

const renderLoginPage = () => {
  const contextValue = {
    user: null,
    login: mockLogin,
    logout: () => {},
    loading: false,
    isAdmin: () => false,
    isSuperAdmin: () => false,
  };

  return render(
    <BrowserRouter>
      <AuthContext.Provider value={contextValue}>
        <LoginPage />
      </AuthContext.Provider>
    </BrowserRouter>
  );
};

describe('LoginPage', () => {
  it('renders login form', () => {
    renderLoginPage();
    expect(screen.getByText('Copropia')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('correo@ejemplo.com')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('********')).toBeInTheDocument();
    expect(screen.getByText('Iniciar Sesion')).toBeInTheDocument();
  });

  it('renders email and password inputs', () => {
    renderLoginPage();
    expect(screen.getByPlaceholderText('correo@ejemplo.com')).toHaveAttribute('type', 'email');
    expect(screen.getByPlaceholderText('********')).toHaveAttribute('type', 'password');
  });

  it('allows typing in email field', async () => {
    renderLoginPage();
    const emailInput = screen.getByPlaceholderText('correo@ejemplo.com');
    await userEvent.type(emailInput, 'admin@test.com');
    expect(emailInput).toHaveValue('admin@test.com');
  });

  it('allows typing in password field', async () => {
    renderLoginPage();
    const passInput = screen.getByPlaceholderText('********');
    await userEvent.type(passInput, 'secret123');
    expect(passInput).toHaveValue('secret123');
  });

  it('calls login on form submit', async () => {
    mockLogin.mockResolvedValueOnce({ email: 'admin@test.com', rol: 'SUPER_ADMIN' });
    renderLoginPage();

    await userEvent.type(screen.getByPlaceholderText('correo@ejemplo.com'), 'admin@test.com');
    await userEvent.type(screen.getByPlaceholderText('********'), 'password123');
    await userEvent.click(screen.getByText('Iniciar Sesion'));

    expect(mockLogin).toHaveBeenCalledWith('admin@test.com', 'password123');
  });
});
