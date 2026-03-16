import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Badge from './Badge';

describe('Badge', () => {
  it('renders text', () => {
    render(<Badge text="ACTIVA" />);
    expect(screen.getByText('ACTIVA')).toBeInTheDocument();
  });

  it('applies correct color class for ACTIVA', () => {
    render(<Badge text="ACTIVA" />);
    expect(screen.getByText('ACTIVA')).toHaveClass('bg-success-50');
  });

  it('applies correct color class for CERRADA', () => {
    render(<Badge text="CERRADA" />);
    expect(screen.getByText('CERRADA')).toHaveClass('bg-gray-100');
  });

  it('applies default color for unknown status', () => {
    render(<Badge text="UNKNOWN" />);
    expect(screen.getByText('UNKNOWN')).toHaveClass('bg-gray-100');
  });
});
