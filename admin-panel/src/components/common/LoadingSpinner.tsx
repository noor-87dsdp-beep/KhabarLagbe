import clsx from 'clsx';

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  color?: 'primary' | 'white' | 'gray';
  className?: string;
  fullScreen?: boolean;
  text?: string;
}

const sizeClasses = {
  sm: 'h-4 w-4 border-2',
  md: 'h-8 w-8 border-2',
  lg: 'h-12 w-12 border-3',
};

const colorClasses = {
  primary: 'border-primary-500 border-t-transparent',
  white: 'border-white border-t-transparent',
  gray: 'border-gray-400 border-t-transparent',
};

export default function LoadingSpinner({
  size = 'md',
  color = 'primary',
  className,
  fullScreen = false,
  text,
}: LoadingSpinnerProps) {
  const spinner = (
    <div
      className={clsx(
        'rounded-full animate-spin',
        sizeClasses[size],
        colorClasses[color],
        className
      )}
    />
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-white bg-opacity-90 flex flex-col items-center justify-center z-50">
        {spinner}
        {text && <p className="mt-4 text-gray-600">{text}</p>}
      </div>
    );
  }

  if (text) {
    return (
      <div className="flex flex-col items-center justify-center py-8">
        {spinner}
        <p className="mt-4 text-gray-600">{text}</p>
      </div>
    );
  }

  return spinner;
}
