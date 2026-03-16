export function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('es-CO', {
    year: 'numeric', month: 'short', day: 'numeric',
  });
}

export function formatDateTime(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('es-CO', {
    year: 'numeric', month: 'short', day: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

export function formatTime(timeStr) {
  if (!timeStr) return '';
  return timeStr.substring(0, 5);
}

export function formatPercent(value) {
  if (value == null) return '0%';
  return `${Number(value).toFixed(2)}%`;
}

export const estadoColors = {
  ACTIVA: 'bg-success-50 text-success-700',
  INACTIVA: 'bg-gray-100 text-gray-600',
  SUSPENDIDA: 'bg-warning-50 text-warning-700',
  PROGRAMADA: 'bg-primary-50 text-primary-700',
  ABIERTA: 'bg-success-50 text-success-700',
  CERRADA: 'bg-gray-100 text-gray-600',
  CANCELADA: 'bg-danger-50 text-danger-700',
  PENDIENTE: 'bg-warning-50 text-warning-700',
  CONFIRMADA: 'bg-success-50 text-success-700',
  COMPLETADA: 'bg-primary-50 text-primary-700',
};
