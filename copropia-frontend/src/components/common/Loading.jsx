import { Loader2 } from 'lucide-react';

export default function Loading({ message = 'Cargando...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-12">
      <Loader2 className="w-8 h-8 animate-spin text-primary-600" />
      <p className="mt-3 text-sm text-gray-500">{message}</p>
    </div>
  );
}
