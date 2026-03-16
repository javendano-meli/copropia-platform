import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import Input from './Input';

describe('Input', () => {
  it('renders with label', () => {
    render(<Input label="Email" />);
    expect(screen.getByText('Email')).toBeInTheDocument();
  });

  it('renders input element', () => {
    render(<Input placeholder="Enter text" />);
    expect(screen.getByPlaceholderText('Enter text')).toBeInTheDocument();
  });

  it('shows error message', () => {
    render(<Input error="Campo requerido" />);
    expect(screen.getByText('Campo requerido')).toBeInTheDocument();
  });

  it('applies error border class', () => {
    const { container } = render(<Input error="Error" />);
    expect(container.querySelector('input')).toHaveClass('border-danger-500');
  });

  it('calls onChange', async () => {
    const onChange = vi.fn();
    render(<Input onChange={onChange} placeholder="Type" />);
    await userEvent.type(screen.getByPlaceholderText('Type'), 'hello');
    expect(onChange).toHaveBeenCalled();
  });
});
