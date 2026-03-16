import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import EmptyState from './EmptyState';

describe('EmptyState', () => {
  it('renders title', () => {
    render(<EmptyState title="No hay datos" />);
    expect(screen.getByText('No hay datos')).toBeInTheDocument();
  });

  it('renders description', () => {
    render(<EmptyState title="Vacio" description="Agrega un registro" />);
    expect(screen.getByText('Agrega un registro')).toBeInTheDocument();
  });

  it('renders action button', () => {
    const onAction = vi.fn();
    render(<EmptyState title="Vacio" actionLabel="Crear" onAction={onAction} />);
    expect(screen.getByText('Crear')).toBeInTheDocument();
  });

  it('calls onAction when button clicked', async () => {
    const onAction = vi.fn();
    render(<EmptyState title="Vacio" actionLabel="Crear" onAction={onAction} />);
    await userEvent.click(screen.getByText('Crear'));
    expect(onAction).toHaveBeenCalledTimes(1);
  });

  it('does not render button without actionLabel', () => {
    render(<EmptyState title="Vacio" />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });
});
