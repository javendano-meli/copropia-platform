import { estadoColors } from '../../utils/formatters';

export default function Badge({ text, className = '' }) {
  const colorClass = estadoColors[text] || 'bg-gray-100 text-gray-600';
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${colorClass} ${className}`}>
      {text}
    </span>
  );
}
