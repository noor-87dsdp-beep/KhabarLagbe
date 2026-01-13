export default function Dashboard() {
  const stats = [
    { label: 'আজকের অর্ডার', value: '1,234', change: '+12%', color: 'bg-blue-500' },
    { label: 'আজকের আয়', value: '৳5,67,890', change: '+8%', color: 'bg-green-500' },
    { label: 'অ্যাক্টিভ রাইডার', value: '456', change: '+5%', color: 'bg-yellow-500' },
    { label: 'অ্যাক্টিভ রেস্টুরেন্ট', value: '789', change: '+3%', color: 'bg-purple-500' },
  ]

  const recentOrders = [
    { id: '#12345', customer: 'রহিম আহমেদ', restaurant: 'স্বাদ রেস্টুরেন্ট', amount: '৳650', status: 'Delivered' },
    { id: '#12346', customer: 'করিম মিয়া', restaurant: 'খাবার ঘর', amount: '৳890', status: 'In Transit' },
    { id: '#12347', customer: 'সালমা বেগম', restaurant: 'বিরিয়ানি হাউস', amount: '৳1,250', status: 'Preparing' },
  ]

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-800">ড্যাশবোর্ড</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => (
          <div key={index} className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-500 text-sm">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-800 mt-1">{stat.value}</p>
                <p className="text-green-500 text-sm mt-1">{stat.change}</p>
              </div>
              <div className={`${stat.color} w-12 h-12 rounded-full flex items-center justify-center text-white text-2xl`}>
                {index === 0 ? '📦' : index === 1 ? '💰' : index === 2 ? '🏍️' : '🍽️'}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Recent Orders */}
      <div className="bg-white rounded-lg shadow">
        <div className="p-6 border-b">
          <h2 className="text-xl font-bold text-gray-800">সাম্প্রতিক অর্ডার</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">অর্ডার ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">গ্রাহক</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">রেস্টুরেন্ট</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">পরিমাণ</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">স্ট্যাটাস</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {recentOrders.map((order) => (
                <tr key={order.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{order.id}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{order.customer}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{order.restaurant}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{order.amount}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                      order.status === 'Delivered' ? 'bg-green-100 text-green-800' :
                      order.status === 'In Transit' ? 'bg-blue-100 text-blue-800' :
                      'bg-yellow-100 text-yellow-800'
                    }`}>
                      {order.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pending Approvals */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-bold text-gray-800 mb-4">রেস্টুরেন্ট অনুমোদন অপেক্ষারত</h3>
          <div className="space-y-3">
            {['নতুন স্বাদ রেস্টুরেন্ট', 'বার্গার কিং বাংলাদেশ', 'পিৎজা প্লাস'].map((name, i) => (
              <div key={i} className="flex items-center justify-between p-3 bg-gray-50 rounded">
                <span className="text-sm text-gray-700">{name}</span>
                <div className="space-x-2">
                  <button className="px-3 py-1 text-xs bg-green-500 text-white rounded hover:bg-green-600">অনুমোদন</button>
                  <button className="px-3 py-1 text-xs bg-red-500 text-white rounded hover:bg-red-600">বাতিল</button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-bold text-gray-800 mb-4">রাইডার আবেদন</h3>
          <div className="space-y-3">
            {['আব্দুল করিম', 'রফিকুল ইসলাম', 'সাকিব হাসান'].map((name, i) => (
              <div key={i} className="flex items-center justify-between p-3 bg-gray-50 rounded">
                <span className="text-sm text-gray-700">{name}</span>
                <div className="space-x-2">
                  <button className="px-3 py-1 text-xs bg-green-500 text-white rounded hover:bg-green-600">অনুমোদন</button>
                  <button className="px-3 py-1 text-xs bg-red-500 text-white rounded hover:bg-red-600">বাতিল</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
