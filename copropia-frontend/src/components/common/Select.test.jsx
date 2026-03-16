import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Select from './Select';

describe('Select', () => {
  const options = [
    { value: 'a', label: 'Option A' },
    { value: 'b', label: 'Option B' },
  ];

  it('renders with label', () => {
    render(<Select label="Choose" options={options} />);
    expect(screen.getByText('Choose')).toBeInTheDocument();
  });

  it('renders options', () => {
    render(<Select options={options} />);
    expect(screen.getByText('Option A')).toBeInTheDocument();
    expect(screen.getByText('Option B')).toBeInTheDocument();
  });

  it('renders default placeholder option', () => {
    render(<Select options={options} />);
    expect(screen.getByText('Seleccionar...')).toBeInTheDocument();
  });

  it('shows error message', () => {
    render(<Select options={options} error="Requerido" />);
    expect(screen.getByText('Requerido')).toBeInTheDocument();
  });
});
