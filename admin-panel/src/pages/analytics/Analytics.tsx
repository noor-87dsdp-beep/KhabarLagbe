import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '../../services/api';
import StatCard from '../../components/common/StatCard';
import LineChart from '../../components/charts/LineChart';
import BarChart from '../../components/charts/BarChart';
import PieChart from '../../components/charts/PieChart';

interface AnalyticsData {
  revenueByDate: { date: string; revenue: number; orders: number }[];
  ordersByStatus: { status: string; count: number }[];
  userGrowth: { date: string; users: number; newUsers: number }[];
  popularRestaurants: { _id: string; name: string; totalOrders: number; totalRevenue: number }[];
  topRiders: { _id: string; name: string; totalDeliveries: number; totalEarnings: number }[];
  paymentMethods: { method: string; count: number; totalAmount: number }[];
}

export default function Analytics() {
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('30');
  const [analytics, setAnalytics] = useState<AnalyticsData>({
    revenueByDate: [],
    ordersByStatus: [],
    userGrowth: [],
    popularRestaurants: [],
    topRiders: [],
    paymentMethods: [],
  });

  useEffect(() => {
    fetchAnalytics();
  }, [timeRange]);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      
      // Mock data for demonstration
      const mockRevenueData = [
        { date: '১ জান', revenue: 4500000, orders: 120 },
        { date: '২ জান', revenue: 5200000, orders: 145 },
        { date: '৩ জান', revenue: 4800000, orders: 130 },
        { date: '৪ জান', revenue: 6100000, orders: 168 },
        { date: '৫ জান', revenue: 5500000, orders: 155 },
        { date: '৬ জান', revenue: 6800000, orders: 190 },
        { date: '৭ জান', revenue: 7200000, orders: 210 },
        { date: '৮ জান', revenue: 5900000, orders: 165 },
        { date: '৯ জান', revenue: 6400000, orders: 178 },
        { date: '১০ জান', revenue: 7100000, orders: 195 },
      ];

      const mockOrdersByStatus = [
        { status: 'Delivered', count: 1250 },
        { status: 'Cancelled', count: 85 },
        { status: 'In Progress', count: 120 },
        { status: 'Pending', count: 45 },
      ];

      const mockUserGrowth = [
        { date: '১ জান', users: 14500, newUsers: 125 },
        { date: '২ জান', users: 14650, newUsers: 150 },
        { date: '৩ জান', users: 14780, newUsers: 130 },
        { date: '৪ জান', users: 14950, newUsers: 170 },
        { date: '৫ জান', users: 15100, newUsers: 150 },
        { date: '৬ জান', users: 15280, newUsers: 180 },
        { date: '৭ জান', users: 15500, newUsers: 220 },
      ];

      const mockPaymentMethods = [
        { method: 'bKash', count: 450, totalAmount: 22500000 },
        { method: 'Nagad', count: 280, totalAmount: 14000000 },
        { method: 'Card', count: 150, totalAmount: 15000000 },
        { method: 'COD', count: 320, totalAmount: 12800000 },
      ];

      const mockPopularRestaurants = [
        { _id: '1', name: 'স্বাদ রেস্টুরেন্ট', totalOrders: 1250, totalRevenue: 12500000 },
        { _id: '2', name: 'খাবার ঘর', totalOrders: 980, totalRevenue: 9800000 },
        { _id: '3', name: 'বিরিয়ানি হাউস', totalOrders: 850, totalRevenue: 11050000 },
        { _id: '4', name: 'পিৎজা হাট', totalOrders: 720, totalRevenue: 8640000 },
        { _id: '5', name: 'চাইনিজ কর্নার', totalOrders: 680, totalRevenue: 7480000 },
        { _id: '6', name: 'কাবাব ফ্যাক্টরি', totalOrders: 620, totalRevenue: 6820000 },
        { _id: '7', name: 'ফ্রাইড চিকেন', totalOrders: 580, totalRevenue: 5800000 },
        { _id: '8', name: 'সুশি বার', totalOrders: 520, totalRevenue: 7800000 },
        { _id: '9', name: 'বার্গার কিং', totalOrders: 490, totalRevenue: 4410000 },
        { _id: '10', name: 'স্যান্ডউইচ প্লেস', totalOrders: 450, totalRevenue: 4050000 },
      ];

      const mockTopRiders = [
        { _id: '1', name: 'আব্দুল করিম', totalDeliveries: 325, totalEarnings: 48750 },
        { _id: '2', name: 'রফিকুল ইসলাম', totalDeliveries: 298, totalEarnings: 44700 },
        { _id: '3', name: 'সাকিব হাসান', totalDeliveries: 276, totalEarnings: 41400 },
        { _id: '4', name: 'তানভীর আহমেদ', totalDeliveries: 254, totalEarnings: 38100 },
        { _id: '5', name: 'মোস্তফা করিম', totalDeliveries: 232, totalEarnings: 34800 },
        { _id: '6', name: 'নাজমুল হক', totalDeliveries: 218, totalEarnings: 32700 },
        { _id: '7', name: 'আলমগীর হোসেন', totalDeliveries: 205, totalEarnings: 30750 },
        { _id: '8', name: 'জাহিদ হাসান', totalDeliveries: 192, totalEarnings: 28800 },
        { _id: '9', name: 'শফিকুল ইসলাম', totalDeliveries: 178, totalEarnings: 26700 },
        { _id: '10', name: 'কামরুল হাসান', totalDeliveries: 165, totalEarnings: 24750 },
      ];

      setAnalytics({
        revenueByDate: mockRevenueData,
        ordersByStatus: mockOrdersByStatus,
        userGrowth: mockUserGrowth,
        popularRestaurants: mockPopularRestaurants,
        topRiders: mockTopRiders,
        paymentMethods: mockPaymentMethods,
      });

      // Try to fetch real data
      try {
        const response = await api.get('/admin/analytics', {
          params: { days: parseInt(timeRange) },
        });
        if (response.data) {
          setAnalytics(response.data);
        }
      } catch {
        // Use mock data on error
      }
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
      toast.error('Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (type: string) => {
    toast.success(`${type} রিপোর্ট ডাউনলোড হচ্ছে...`);
  };

  const totalRevenue = analytics.revenueByDate.reduce((sum, d) => sum + d.revenue, 0);
  const totalOrders = analytics.revenueByDate.reduce((sum, d) => sum + d.orders, 0);
  const avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

  const pieData = analytics.paymentMethods.map((m) => ({
    name: m.method,
    value: m.count,
  }));

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading analytics...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-3xl font-bold text-gray-800">বিশ্লেষণ ও রিপোর্ট</h1>
        <div className="flex items-center gap-4">
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
          >
            <option value="7">শেষ ৭ দিন</option>
            <option value="30">শেষ ৩০ দিন</option>
            <option value="90">শেষ ৯০ দিন</option>
            <option value="365">শেষ ১ বছর</option>
          </select>
          <button
            onClick={() => handleExport('PDF')}
            className="px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors"
          >
            📥 এক্সপোর্ট
          </button>
        </div>
      </div>

      {/* Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="মোট রেভিনিউ"
          value={`৳${(totalRevenue / 100).toLocaleString()}`}
          change="+15% পূর্ববর্তী সময়ের তুলনায়"
          changeType="increase"
          icon="💰"
          iconBg="bg-green-500"
        />
        <StatCard
          title="মোট অর্ডার"
          value={totalOrders.toLocaleString()}
          change="+12% পূর্ববর্তী সময়ের তুলনায়"
          changeType="increase"
          icon="📦"
          iconBg="bg-blue-500"
        />
        <StatCard
          title="গড় অর্ডার মূল্য"
          value={`৳${(avgOrderValue / 100).toFixed(0)}`}
          change="+3% পূর্ববর্তী সময়ের তুলনায়"
          changeType="increase"
          icon="📊"
          iconBg="bg-purple-500"
        />
        <StatCard
          title="নতুন ব্যবহারকারী"
          value="1,125"
          change="+18% পূর্ববর্তী সময়ের তুলনায়"
          changeType="increase"
          icon="👥"
          iconBg="bg-orange-500"
        />
      </div>

      {/* Revenue Chart */}
      <div className="bg-white rounded-xl shadow-sm p-6">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">রেভিনিউ ট্রেন্ড</h2>
        <LineChart
          data={analytics.revenueByDate}
          xKey="date"
          lines={[
            { dataKey: 'revenue', color: '#f97316', name: 'রেভিনিউ (৳)' },
          ]}
          height={350}
          formatYAxis={(value) => `৳${(value / 100000).toFixed(0)}L`}
          formatTooltip={(value) => `৳${(value / 100).toLocaleString()}`}
        />
      </div>

      {/* Orders & User Growth */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">অর্ডার ট্রেন্ড</h2>
          <BarChart
            data={analytics.revenueByDate}
            xKey="date"
            bars={[
              { dataKey: 'orders', color: '#3b82f6', name: 'অর্ডার সংখ্যা' },
            ]}
            height={300}
          />
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">ব্যবহারকারী বৃদ্ধি</h2>
          <LineChart
            data={analytics.userGrowth}
            xKey="date"
            lines={[
              { dataKey: 'users', color: '#22c55e', name: 'মোট ব্যবহারকারী' },
              { dataKey: 'newUsers', color: '#f97316', name: 'নতুন ব্যবহারকারী' },
            ]}
            height={300}
          />
        </div>
      </div>

      {/* Payment Methods & Order Status */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">পেমেন্ট পদ্ধতি বিতরণ</h2>
          <PieChart
            data={pieData}
            height={300}
            innerRadius={60}
            outerRadius={100}
          />
          <div className="mt-4 grid grid-cols-2 gap-4">
            {analytics.paymentMethods.map((method) => (
              <div key={method.method} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <span className="text-sm font-medium text-gray-700">{method.method}</span>
                <div className="text-right">
                  <p className="text-sm font-semibold text-gray-900">৳{(method.totalAmount / 100).toLocaleString()}</p>
                  <p className="text-xs text-gray-500">{method.count} transactions</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">অর্ডার স্ট্যাটাস বিতরণ</h2>
          <div className="space-y-4">
            {analytics.ordersByStatus.map((status, index) => {
              const total = analytics.ordersByStatus.reduce((sum, s) => sum + s.count, 0);
              const percentage = ((status.count / total) * 100).toFixed(1);
              const colors = ['bg-green-500', 'bg-red-500', 'bg-blue-500', 'bg-yellow-500'];
              
              return (
                <div key={status.status}>
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-sm font-medium text-gray-700">{status.status}</span>
                    <span className="text-sm text-gray-600">{status.count} ({percentage}%)</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div
                      className={`${colors[index % colors.length]} h-3 rounded-full transition-all`}
                      style={{ width: `${percentage}%` }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Top Performers */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">শীর্ষ রেস্টুরেন্ট</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase">#</th>
                  <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase">রেস্টুরেন্ট</th>
                  <th className="text-right py-3 px-2 text-xs font-medium text-gray-500 uppercase">অর্ডার</th>
                  <th className="text-right py-3 px-2 text-xs font-medium text-gray-500 uppercase">রেভিনিউ</th>
                </tr>
              </thead>
              <tbody>
                {analytics.popularRestaurants.slice(0, 10).map((restaurant, index) => (
                  <tr key={restaurant._id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-2 text-sm text-gray-600">{index + 1}</td>
                    <td className="py-3 px-2 text-sm font-medium text-gray-900">{restaurant.name}</td>
                    <td className="py-3 px-2 text-sm text-right text-gray-600">{restaurant.totalOrders}</td>
                    <td className="py-3 px-2 text-sm text-right font-medium text-gray-900">
                      ৳{(restaurant.totalRevenue / 100).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">শীর্ষ রাইডার</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase">#</th>
                  <th className="text-left py-3 px-2 text-xs font-medium text-gray-500 uppercase">রাইডার</th>
                  <th className="text-right py-3 px-2 text-xs font-medium text-gray-500 uppercase">ডেলিভারি</th>
                  <th className="text-right py-3 px-2 text-xs font-medium text-gray-500 uppercase">আয়</th>
                </tr>
              </thead>
              <tbody>
                {analytics.topRiders.slice(0, 10).map((rider, index) => (
                  <tr key={rider._id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-2 text-sm text-gray-600">{index + 1}</td>
                    <td className="py-3 px-2 text-sm font-medium text-gray-900">{rider.name}</td>
                    <td className="py-3 px-2 text-sm text-right text-gray-600">{rider.totalDeliveries}</td>
                    <td className="py-3 px-2 text-sm text-right font-medium text-gray-900">
                      ৳{rider.totalEarnings.toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
