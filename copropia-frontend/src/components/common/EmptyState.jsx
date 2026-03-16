import { Inbox } from 'lucide-react';
import Button from './Button';

export default function EmptyState({ title, description, actionLabel, onAction }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center">
      <Inbox className="w-12 h-12 text-gray-300 mb-4" />
      <h3 className="text-lg font-medium text-gray-900">{title}</h3>
      {description && <p className="mt-1 text-sm text-gray-500">{description}</p>}
      {actionLabel && onAction && (
        <Button className="mt-4" onClick={onAction}>{actionLabel}</Button>
      )}
    </div>
  );
}
