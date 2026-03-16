import { describe, it, expect } from 'vitest';
import { formatDate, formatDateTime, formatTime, formatPercent, estadoColors } from './formatters';

describe('formatters', () => {
  describe('formatDate', () => {
    it('formats date string', () => {
      const result = formatDate('2026-03-15');
      expect(result).toContain('2026');
    });

    it('returns empty string for null', () => {
      expect(formatDate(null)).toBe('');
    });

    it('returns empty string for undefined', () => {
      expect(formatDate(undefined)).toBe('');
    });
  });

  describe('formatDateTime', () => {
    it('formats datetime string', () => {
      const result = formatDateTime('2026-03-15T14:00:00');
      expect(result).toContain('2026');
    });

    it('returns empty for null', () => {
      expect(formatDateTime(null)).toBe('');
    });
  });

  describe('formatTime', () => {
    it('extracts HH:MM from time string', () => {
      expect(formatTime('14:30:00')).toBe('14:30');
    });

    it('returns empty for null', () => {
      expect(formatTime(null)).toBe('');
    });
  });

  describe('formatPercent', () => {
    it('formats number as percentage', () => {
      expect(formatPercent(75.5)).toBe('75.50%');
    });

    it('returns 0% for null', () => {
      expect(formatPercent(null)).toBe('0%');
    });

    it('returns 0% for undefined', () => {
      expect(formatPercent(undefined)).toBe('0%');
    });
  });

  describe('estadoColors', () => {
    it('has color for ACTIVA', () => {
      expect(estadoColors.ACTIVA).toContain('success');
    });

    it('has color for CERRADA', () => {
      expect(estadoColors.CERRADA).toContain('gray');
    });

    it('has color for PENDIENTE', () => {
      expect(estadoColors.PENDIENTE).toContain('warning');
    });

    it('has color for CANCELADA', () => {
      expect(estadoColors.CANCELADA).toContain('danger');
    });
  });
});
