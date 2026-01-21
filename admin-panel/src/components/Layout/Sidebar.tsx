import { Link, useLocation } from 'react-router-dom'
import clsx from 'clsx'

interface SidebarProps {
  isOpen: boolean
}

const menuItems = [
  { path: '/dashboard', label: 'ড্যাশবোর্ড', icon: '📊' },
  { path: '/orders', label: 'অর্ডার', icon: '📦' },
  { path: '/users', label: 'ব্যবহারকারী', icon: '👥' },
  { path: '/restaurants', label: 'রেস্টুরেন্ট', icon: '🍽️' },
  { path: '/riders', label: 'রাইডার', icon: '🏍️' },
  { path: '/payments', label: 'পেমেন্ট', icon: '💰' },
  { path: '/promo-codes', label: 'প্রোমো কোড', icon: '🎟️' },
  { path: '/zones', label: 'জোন ব্যবস্থাপনা', icon: '🗺️' },
  { path: '/analytics', label: 'বিশ্লেষণ', icon: '📈' },
  { path: '/settings', label: 'সেটিংস', icon: '⚙️' },
]

export default function Sidebar({ isOpen }: SidebarProps) {
  const location = useLocation()

  if (!isOpen) return null

  return (
    <div className="w-64 bg-white shadow-lg flex flex-col h-full">
      {/* Logo */}
      <div className="p-6 border-b">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-br from-orange-500 to-red-500 rounded-xl flex items-center justify-center">
            <span className="text-white text-xl">🍽️</span>
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-800">KhabarLagbe</h1>
            <p className="text-xs text-gray-500">Admin Panel</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 overflow-y-auto">
        <div className="space-y-1">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path || 
              (item.path !== '/dashboard' && location.pathname.startsWith(item.path))
            
            return (
              <Link
                key={item.path}
                to={item.path}
                className={clsx(
                  'flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-200',
                  isActive
                    ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-lg shadow-orange-500/30'
                    : 'text-gray-700 hover:bg-orange-50 hover:text-orange-600'
                )}
              >
                <span className="text-xl">{item.icon}</span>
                <span className="font-medium">{item.label}</span>
                {isActive && (
                  <span className="ml-auto">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                    </svg>
                  </span>
                )}
              </Link>
            )
          })}
        </div>
      </nav>

      {/* Footer */}
      <div className="p-4 border-t">
        <div className="bg-gradient-to-r from-orange-50 to-red-50 rounded-lg p-4">
          <p className="text-xs text-gray-600 mb-2">সাহায্য প্রয়োজন?</p>
          <a 
            href="mailto:support@khabarlagbe.com" 
            className="text-sm text-orange-600 hover:text-orange-700 font-medium"
          >
            সাপোর্টে যোগাযোগ করুন
          </a>
        </div>
        <p className="text-xs text-gray-400 text-center mt-4">
          v1.0.0 © {new Date().getFullYear()}
        </p>
      </div>
    </div>
  )
}
