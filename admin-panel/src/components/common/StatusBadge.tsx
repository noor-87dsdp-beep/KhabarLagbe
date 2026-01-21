import clsx from 'clsx';

type StatusType =
  | 'success'
  | 'warning'
  | 'error'
  | 'info'
  | 'pending'
  | 'active'
  | 'inactive'
  | 'default';

interface StatusBadgeProps {
  status: string;
  type?: StatusType;
  size?: 'sm' | 'md' | 'lg';
  dot?: boolean;
}

const statusTypeMap: Record<string, StatusType> = {
  // Order statuses
  pending: 'warning',
  confirmed: 'info',
  preparing: 'info',
  ready: 'info',
  picked_up: 'info',
  on_the_way: 'info',
  delivered: 'success',
  cancelled: 'error',
  
  // Payment statuses
  success: 'success',
  failed: 'error',
  initiated: 'info',
  refunded: 'warning',
  
  // Approval statuses
  approved: 'success',
  rejected: 'error',
  
  // User/Rider statuses
  active: 'active',
  inactive: 'inactive',
  available: 'success',
  busy: 'warning',
  offline: 'inactive',
  on_break: 'warning',
  
  // Verification
  verified: 'success',
  unverified: 'warning',
  
  // Boolean
  true: 'success',
  false: 'error',
  
  // Generic
  open: 'success',
  closed: 'inactive',
};

const typeClasses: Record<StatusType, string> = {
  success: 'bg-green-100 text-green-800 border-green-200',
  warning: 'bg-yellow-100 text-yellow-800 border-yellow-200',
  error: 'bg-red-100 text-red-800 border-red-200',
  info: 'bg-blue-100 text-blue-800 border-blue-200',
  pending: 'bg-orange-100 text-orange-800 border-orange-200',
  active: 'bg-emerald-100 text-emerald-800 border-emerald-200',
  inactive: 'bg-gray-100 text-gray-800 border-gray-200',
  default: 'bg-gray-100 text-gray-700 border-gray-200',
};

const dotClasses: Record<StatusType, string> = {
  success: 'bg-green-500',
  warning: 'bg-yellow-500',
  error: 'bg-red-500',
  info: 'bg-blue-500',
  pending: 'bg-orange-500',
  active: 'bg-emerald-500',
  inactive: 'bg-gray-400',
  default: 'bg-gray-400',
};

const sizeClasses = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-2.5 py-1 text-xs',
  lg: 'px-3 py-1.5 text-sm',
};

export default function StatusBadge({ status, type, size = 'md', dot = false }: StatusBadgeProps) {
  const normalizedStatus = status.toLowerCase().replace(/\s+/g, '_');
  const resolvedType = type || statusTypeMap[normalizedStatus] || 'default';
  
  const displayText = status
    .replace(/_/g, ' ')
    .replace(/\b\w/g, (char) => char.toUpperCase());

  return (
    <span
      className={clsx(
        'inline-flex items-center font-semibold rounded-full border',
        typeClasses[resolvedType],
        sizeClasses[size]
      )}
    >
      {dot && (
        <span
          className={clsx('w-2 h-2 rounded-full mr-1.5', dotClasses[resolvedType])}
        />
      )}
      {displayText}
    </span>
  );
}
