import { Link, useLocation } from 'react-router-dom'

interface SidebarProps {
  isOpen: boolean
}

export default function Sidebar({ isOpen }: SidebarProps) {
  const location = useLocation()

  const menuItems = [
    { path: '/dashboard', label: 'ড্যাশবোর্ড', icon: '📊' },
    { path: '/users', label: 'ব্যবহারকারী', icon: '👥' },
    { path: '/restaurants', label: 'রেস্টুরেন্ট', icon: '🍽️' },
    { path: '/riders', label: 'রাইডার', icon: '🏍️' },
    { path: '/orders', label: 'অর্ডার', icon: '📦' },
    { path: '/payments', label: 'পেমেন্ট', icon: '💰' },
    { path: '/marketing', label: 'মার্কেটিং', icon: '📢' },
    { path: '/analytics', label: 'বিশ্লেষণ', icon: '📈' },
    { path: '/settings', label: 'সেটিংস', icon: '⚙️' },
  ]

  if (!isOpen) return null

  return (
    <div className="w-64 bg-white shadow-lg">
      <div className="p-6 border-b">
        <h1 className="text-2xl font-bold text-primary-600">KhabarLagbe</h1>
        <p className="text-sm text-gray-500">Admin Panel</p>
      </div>
      <nav className="p-4">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`flex items-center space-x-3 px-4 py-3 rounded-lg mb-2 transition-colors ${
              location.pathname === item.path
                ? 'bg-primary-50 text-primary-600'
                : 'text-gray-700 hover:bg-gray-100'
            }`}
          >
            <span className="text-xl">{item.icon}</span>
            <span className="font-medium">{item.label}</span>
          </Link>
        ))}
      </nav>
    </div>
  )
}
