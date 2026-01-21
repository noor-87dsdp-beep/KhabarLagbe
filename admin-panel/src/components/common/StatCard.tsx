import { ReactNode } from 'react';
import clsx from 'clsx';

interface StatCardProps {
  title: string;
  value: string | number;
  change?: string;
  changeType?: 'increase' | 'decrease' | 'neutral';
  icon?: ReactNode;
  iconBg?: string;
  loading?: boolean;
}

export default function StatCard({
  title,
  value,
  change,
  changeType = 'neutral',
  icon,
  iconBg = 'bg-primary-500',
  loading = false,
}: StatCardProps) {
  if (loading) {
    return (
      <div className="bg-white rounded-xl shadow-sm p-6 animate-pulse">
        <div className="flex items-center justify-between">
          <div className="space-y-3 flex-1">
            <div className="h-4 bg-gray-200 rounded w-24"></div>
            <div className="h-8 bg-gray-200 rounded w-32"></div>
            <div className="h-3 bg-gray-200 rounded w-16"></div>
          </div>
          <div className="w-14 h-14 bg-gray-200 rounded-full"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-gray-500 text-sm font-medium">{title}</p>
          <p className="text-2xl font-bold text-gray-800 mt-1">{value}</p>
          {change && (
            <p
              className={clsx('text-sm mt-1 flex items-center', {
                'text-green-500': changeType === 'increase',
                'text-red-500': changeType === 'decrease',
                'text-gray-500': changeType === 'neutral',
              })}
            >
              {changeType === 'increase' && (
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 17l5-5 5 5M7 7l5 5 5-5" />
                </svg>
              )}
              {changeType === 'decrease' && (
                <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 7l-5 5-5-5m10 10l-5-5-5 5" />
                </svg>
              )}
              {change}
            </p>
          )}
        </div>
        {icon && (
          <div
            className={clsx(
              'w-14 h-14 rounded-full flex items-center justify-center text-white text-2xl',
              iconBg
            )}
          >
            {icon}
          </div>
        )}
      </div>
    </div>
  );
}
