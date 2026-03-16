import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Table from './Table';

describe('Table', () => {
  const columns = [
    { key: 'name', label: 'Nombre' },
    { key: 'city', label: 'Ciudad' },
  ];

  const data = [
    { id: 1, name: 'Torres', city: 'Bogota' },
    { id: 2, name: 'Central', city: 'Medellin' },
  ];

  it('renders column headers', () => {
    render(<Table columns={columns} data={data} />);
    expect(screen.getByText('Nombre')).toBeInTheDocument();
    expect(screen.getByText('Ciudad')).toBeInTheDocument();
  });

  it('renders data rows', () => {
    render(<Table columns={columns} data={data} />);
    expect(screen.getByText('Torres')).toBeInTheDocument();
    expect(screen.getByText('Medellin')).toBeInTheDocument();
  });

  it('shows empty message when no data', () => {
    render(<Table columns={columns} data={[]} />);
    expect(screen.getByText('No hay datos')).toBeInTheDocument();
  });

  it('shows custom empty message', () => {
    render(<Table columns={columns} data={[]} emptyMessage="Sin registros" />);
    expect(screen.getByText('Sin registros')).toBeInTheDocument();
  });

  it('uses custom render function', () => {
    const cols = [{ key: 'name', label: 'Nombre', render: (row) => `>> ${row.name}` }];
    render(<Table columns={cols} data={[{ id: 1, name: 'Test' }]} />);
    expect(screen.getByText('>> Test')).toBeInTheDocument();
  });
});
