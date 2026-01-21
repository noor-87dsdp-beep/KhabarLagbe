import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import StatCard from '../components/common/StatCard';
import StatusBadge from '../components/common/StatusBadge';
import LineChart from '../components/charts/LineChart';
import api from '../services/api';

interface DashboardStats {
  todayOrders: number;
  todayRevenue: number;
  activeRiders: number;
  activeRestaurants: number;
  totalUsers: number;
  pendingRestaurants: number;
  pendingRiders: number;
}

interface RecentOrder {
  _id: string;
  orderNumber: string;
  userId: { name: string };
  restaurantId: { name: string };
  totalAmount: number;
  status: string;
  createdAt: string;
}

interface RevenueData {
  date: string;
  revenue: number;
  orders: number;
}

interface TopRestaurant {
  _id: string;
  name: string;
  totalOrders: number;
  totalRevenue: number;
  rating: number;
}

interface PendingApproval {
  _id: string;
  name: string;
  phone: string;
  createdAt: string;
}

export default function Dashboard() {
  const [stats, setStats] = useState<DashboardStats>({
    todayOrders: 0,
    todayRevenue: 0,
    activeRiders: 0,
    activeRestaurants: 0,
    totalUsers: 0,
    pendingRestaurants: 0,
    pendingRiders: 0,
  });
  const [recentOrders, setRecentOrders] = useState<RecentOrder[]>([]);
  const [revenueData, setRevenueData] = useState<RevenueData[]>([]);
  const [topRestaurants, setTopRestaurants] = useState<TopRestaurant[]>([]);
  const [pendingRestaurants, setPendingRestaurants] = useState<PendingApproval[]>([]);
  const [pendingRiders, setPendingRiders] = useState<PendingApproval[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Simulated dashboard data - replace with real API calls
      const mockStats: DashboardStats = {
        todayOrders: 1234,
        todayRevenue: 567890,
        activeRiders: 456,
        activeRestaurants: 789,
        totalUsers: 15000,
        pendingRestaurants: 12,
        pendingRiders: 8,
      };
      
      const mockRecentOrders: RecentOrder[] = [
        { _id: '1', orderNumber: 'ORD12345', userId: { name: 'রহিম আহমেদ' }, restaurantId: { name: 'স্বাদ রেস্টুরেন্ট' }, totalAmount: 65000, status: 'delivered', createdAt: new Date().toISOString() },
        { _id: '2', orderNumber: 'ORD12346', userId: { name: 'করিম মিয়া' }, restaurantId: { name: 'খাবার ঘর' }, totalAmount: 89000, status: 'on_the_way', createdAt: new Date().toISOString() },
        { _id: '3', orderNumber: 'ORD12347', userId: { name: 'সালমা বেগম' }, restaurantId: { name: 'বিরিয়ানি হাউস' }, totalAmount: 125000, status: 'preparing', createdAt: new Date().toISOString() },
        { _id: '4', orderNumber: 'ORD12348', userId: { name: 'আবু বকর' }, restaurantId: { name: 'পিৎজা হাট' }, totalAmount: 95000, status: 'confirmed', createdAt: new Date().toISOString() },
        { _id: '5', orderNumber: 'ORD12349', userId: { name: 'ফাতিমা খাতুন' }, restaurantId: { name: 'চাইনিজ কর্নার' }, totalAmount: 78000, status: 'pending', createdAt: new Date().toISOString() },
      ];

      const mockRevenueData: RevenueData[] = [
        { date: 'শনি', revenue: 450000, orders: 120 },
        { date: 'রবি', revenue: 520000, orders: 145 },
        { date: 'সোম', revenue: 480000, orders: 130 },
        { date: 'মঙ্গল', revenue: 610000, orders: 168 },
        { date: 'বুধ', revenue: 550000, orders: 155 },
        { date: 'বৃহ', revenue: 680000, orders: 190 },
        { date: 'শুক্র', revenue: 720000, orders: 210 },
      ];

      const mockTopRestaurants: TopRestaurant[] = [
        { _id: '1', name: 'স্বাদ রেস্টুরেন্ট', totalOrders: 1250, totalRevenue: 1250000, rating: 4.8 },
        { _id: '2', name: 'খাবার ঘর', totalOrders: 980, totalRevenue: 980000, rating: 4.6 },
        { _id: '3', name: 'বিরিয়ানি হাউস', totalOrders: 850, totalRevenue: 1105000, rating: 4.7 },
        { _id: '4', name: 'পিৎজা হাট', totalOrders: 720, totalRevenue: 864000, rating: 4.5 },
        { _id: '5', name: 'চাইনিজ কর্নার', totalOrders: 680, totalRevenue: 748000, rating: 4.4 },
      ];

      const mockPendingRestaurants: PendingApproval[] = [
        { _id: '1', name: 'নতুন স্বাদ রেস্টুরেন্ট', phone: '01712345678', createdAt: new Date().toISOString() },
        { _id: '2', name: 'বার্গার কিং বাংলাদেশ', phone: '01812345678', createdAt: new Date().toISOString() },
        { _id: '3', name: 'পিৎজা প্লাস', phone: '01912345678', createdAt: new Date().toISOString() },
      ];

      const mockPendingRiders: PendingApproval[] = [
        { _id: '1', name: 'আব্দুল করিম', phone: '01712345679', createdAt: new Date().toISOString() },
        { _id: '2', name: 'রফিকুল ইসলাম', phone: '01812345679', createdAt: new Date().toISOString() },
        { _id: '3', name: 'সাকিব হাসান', phone: '01912345679', createdAt: new Date().toISOString() },
      ];

      setStats(mockStats);
      setRecentOrders(mockRecentOrders);
      setRevenueData(mockRevenueData);
      setTopRestaurants(mockTopRestaurants);
      setPendingRestaurants(mockPendingRestaurants);
      setPendingRiders(mockPendingRiders);

      // Try to fetch real data
      try {
        const [statsRes, ordersRes] = await Promise.all([
          api.get('/admin/dashboard/stats').catch(() => null),
          api.get('/admin/orders', { params: { limit: 5 } }).catch(() => null),
        ]);
        
        if (statsRes?.data) setStats(statsRes.data);
        if (ordersRes?.data?.orders) setRecentOrders(ordersRes.data.orders);
      } catch {
        // Use mock data on error
      }
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveRestaurant = async (id: string) => {
    try {
      await api.patch(`/restaurants/admin/${id}/approve`);
      toast.success('Restaurant approved successfully');
      setPendingRestaurants(prev => prev.filter(r => r._id !== id));
      setStats(prev => ({ ...prev, pendingRestaurants: prev.pendingRestaurants - 1 }));
    } catch {
      toast.error('Failed to approve restaurant');
    }
  };

  const handleRejectRestaurant = async (id: string) => {
    try {
      await api.patch(`/restaurants/admin/${id}/reject`, { reason: 'Not meeting requirements' });
      toast.success('Restaurant rejected');
      setPendingRestaurants(prev => prev.filter(r => r._id !== id));
      setStats(prev => ({ ...prev, pendingRestaurants: prev.pendingRestaurants - 1 }));
    } catch {
      toast.error('Failed to reject restaurant');
    }
  };

  const handleApproveRider = async (id: string) => {
    try {
      await api.patch(`/riders/${id}/approve`);
      toast.success('Rider approved successfully');
      setPendingRiders(prev => prev.filter(r => r._id !== id));
      setStats(prev => ({ ...prev, pendingRiders: prev.pendingRiders - 1 }));
    } catch {
      toast.error('Failed to approve rider');
    }
  };

  const handleRejectRider = async (id: string) => {
    try {
      await api.patch(`/riders/${id}/suspend`, { reason: 'Application rejected' });
      toast.success('Rider rejected');
      setPendingRiders(prev => prev.filter(r => r._id !== id));
      setStats(prev => ({ ...prev, pendingRiders: prev.pendingRiders - 1 }));
    } catch {
      toast.error('Failed to reject rider');
    }
  };

  const formatCurrency = (value: number) => `৳${(value / 100).toLocaleString()}`;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-gray-800">ড্যাশবোর্ড</h1>
        <div className="text-sm text-gray-500">
          Last updated: {new Date().toLocaleString('bn-BD')}
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 xl:grid-cols-5 gap-6">
        <StatCard
          title="আজকের অর্ডার"
          value={stats.todayOrders.toLocaleString()}
          change="+12% from yesterday"
          changeType="increase"
          icon="📦"
          iconBg="bg-blue-500"
          loading={loading}
        />
        <StatCard
          title="আজকের আয়"
          value={formatCurrency(stats.todayRevenue)}
          change="+8% from yesterday"
          changeType="increase"
          icon="💰"
          iconBg="bg-green-500"
          loading={loading}
        />
        <StatCard
          title="অ্যাক্টিভ রাইডার"
          value={stats.activeRiders.toLocaleString()}
          change="+5 online"
          changeType="increase"
          icon="🏍️"
          iconBg="bg-yellow-500"
          loading={loading}
        />
        <StatCard
          title="অ্যাক্টিভ রেস্টুরেন্ট"
          value={stats.activeRestaurants.toLocaleString()}
          change="Currently open"
          changeType="neutral"
          icon="🍽️"
          iconBg="bg-purple-500"
          loading={loading}
        />
        <StatCard
          title="মোট ব্যবহারকারী"
          value={stats.totalUsers.toLocaleString()}
          change="+125 today"
          changeType="increase"
          icon="👥"
          iconBg="bg-indigo-500"
          loading={loading}
        />
      </div>

      {/* Revenue Chart & Top Restaurants */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-lg font-semibold text-gray-800">সাপ্তাহিক রেভিনিউ</h2>
            <Link to="/analytics" className="text-primary-600 hover:text-primary-700 text-sm font-medium">
              বিস্তারিত দেখুন →
            </Link>
          </div>
          <LineChart
            data={revenueData}
            xKey="date"
            lines={[
              { dataKey: 'revenue', color: '#f97316', name: 'রেভিনিউ (৳)' },
              { dataKey: 'orders', color: '#3b82f6', name: 'অর্ডার' },
            ]}
            height={280}
            formatYAxis={(value) => `৳${(value / 1000).toFixed(0)}k`}
            formatTooltip={(value) => `৳${value.toLocaleString()}`}
          />
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-800">শীর্ষ রেস্টুরেন্ট</h2>
            <Link to="/restaurants" className="text-primary-600 hover:text-primary-700 text-sm">
              সব দেখুন
            </Link>
          </div>
          <div className="space-y-4">
            {topRestaurants.map((restaurant, index) => (
              <div key={restaurant._id} className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-sm font-bold text-primary-600">
                  {index + 1}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{restaurant.name}</p>
                  <p className="text-xs text-gray-500">{restaurant.totalOrders} orders</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold text-gray-900">
                    ৳{(restaurant.totalRevenue / 100).toLocaleString()}
                  </p>
                  <p className="text-xs text-yellow-600">⭐ {restaurant.rating}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Recent Orders */}
      <div className="bg-white rounded-xl shadow-sm">
        <div className="p-6 border-b flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-800">সাম্প্রতিক অর্ডার</h2>
          <Link to="/orders" className="text-primary-600 hover:text-primary-700 text-sm font-medium">
            সব অর্ডার দেখুন →
          </Link>
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
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">সময়</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {recentOrders.map((order) => (
                <tr key={order._id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <Link to={`/orders/${order._id}`} className="text-sm font-medium text-primary-600 hover:text-primary-700">
                      #{order.orderNumber}
                    </Link>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{order.userId?.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">{order.restaurantId?.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    ৳{(order.totalAmount / 100).toLocaleString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <StatusBadge status={order.status} />
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {new Date(order.createdAt).toLocaleTimeString('bn-BD')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pending Approvals */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">
              রেস্টুরেন্ট অনুমোদন অপেক্ষারত
              <span className="ml-2 px-2 py-0.5 text-xs bg-orange-100 text-orange-700 rounded-full">
                {pendingRestaurants.length}
              </span>
            </h3>
            <Link to="/restaurants?tab=pending" className="text-primary-600 hover:text-primary-700 text-sm">
              সব দেখুন
            </Link>
          </div>
          <div className="space-y-3">
            {pendingRestaurants.length === 0 ? (
              <p className="text-gray-500 text-center py-4">কোন অপেক্ষারত রেস্টুরেন্ট নেই</p>
            ) : (
              pendingRestaurants.map((restaurant) => (
                <div key={restaurant._id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-gray-900">{restaurant.name}</p>
                    <p className="text-xs text-gray-500">{restaurant.phone}</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleApproveRestaurant(restaurant._id)}
                      className="px-3 py-1.5 text-xs bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
                    >
                      অনুমোদন
                    </button>
                    <button
                      onClick={() => handleRejectRestaurant(restaurant._id)}
                      className="px-3 py-1.5 text-xs bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
                    >
                      বাতিল
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-800">
              রাইডার আবেদন
              <span className="ml-2 px-2 py-0.5 text-xs bg-blue-100 text-blue-700 rounded-full">
                {pendingRiders.length}
              </span>
            </h3>
            <Link to="/riders?tab=pending" className="text-primary-600 hover:text-primary-700 text-sm">
              সব দেখুন
            </Link>
          </div>
          <div className="space-y-3">
            {pendingRiders.length === 0 ? (
              <p className="text-gray-500 text-center py-4">কোন অপেক্ষারত রাইডার নেই</p>
            ) : (
              pendingRiders.map((rider) => (
                <div key={rider._id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="text-sm font-medium text-gray-900">{rider.name}</p>
                    <p className="text-xs text-gray-500">{rider.phone}</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleApproveRider(rider._id)}
                      className="px-3 py-1.5 text-xs bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
                    >
                      অনুমোদন
                    </button>
                    <button
                      onClick={() => handleRejectRider(rider._id)}
                      className="px-3 py-1.5 text-xs bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors"
                    >
                      বাতিল
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Quick Stats Footer */}
      <div className="bg-gradient-to-r from-primary-500 to-primary-600 rounded-xl shadow-sm p-6 text-white">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div className="text-center">
            <p className="text-3xl font-bold">{stats.pendingRestaurants}</p>
            <p className="text-sm text-primary-100">অপেক্ষারত রেস্টুরেন্ট</p>
          </div>
          <div className="text-center">
            <p className="text-3xl font-bold">{stats.pendingRiders}</p>
            <p className="text-sm text-primary-100">অপেক্ষারত রাইডার</p>
          </div>
          <div className="text-center">
            <p className="text-3xl font-bold">{stats.activeRiders}</p>
            <p className="text-sm text-primary-100">অনলাইন রাইডার</p>
          </div>
          <div className="text-center">
            <p className="text-3xl font-bold">{stats.activeRestaurants}</p>
            <p className="text-sm text-primary-100">খোলা রেস্টুরেন্ট</p>
          </div>
        </div>
      </div>
    </div>
  );
}
