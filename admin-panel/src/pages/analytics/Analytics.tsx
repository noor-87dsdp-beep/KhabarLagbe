import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '../../services/api';

export default function Analytics() {
  const [loading, setLoading] = useState(true);
  const [analytics, setAnalytics] = useState<any>(null);
  const [timeRange, setTimeRange] = useState('30'); // days

  useEffect(() => {
    fetchAnalytics();
  }, [timeRange]);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      const endDate = new Date();
      const startDate = new Date();
      startDate.setDate(startDate.getDate() - parseInt(timeRange));

      const response = await api.get('/admin/analytics', {
        params: {
          startDate: startDate.toISOString(),
          endDate: endDate.toISOString(),
        },
      });
      setAnalytics(response.data.data);
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
      toast.error('Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading analytics...</div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Analytics & Reports</h1>
        <select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
        >
          <option value="7">Last 7 days</option>
          <option value="30">Last 30 days</option>
          <option value="90">Last 90 days</option>
        </select>
      </div>

      {/* Popular Restaurants */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <h2 className="text-xl font-bold mb-4">Top Performing Restaurants</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left py-3 px-4">Rank</th>
                <th className="text-left py-3 px-4">Restaurant</th>
                <th className="text-left py-3 px-4">Orders</th>
                <th className="text-left py-3 px-4">Revenue</th>
              </tr>
            </thead>
            <tbody>
              {analytics?.popularRestaurants?.slice(0, 10).map((item: any, index: number) => (
                <tr key={item._id} className="border-b hover:bg-gray-50">
                  <td className="py-3 px-4">#{index + 1}</td>
                  <td className="py-3 px-4 font-medium">{item.restaurant?.name}</td>
                  <td className="py-3 px-4">{item.totalOrders}</td>
                  <td className="py-3 px-4">৳{(item.totalRevenue / 100).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Top Riders */}
      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <h2 className="text-xl font-bold mb-4">Top Performing Riders</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full">
            <thead>
              <tr className="border-b">
                <th className="text-left py-3 px-4">Rank</th>
                <th className="text-left py-3 px-4">Rider</th>
                <th className="text-left py-3 px-4">Deliveries</th>
                <th className="text-left py-3 px-4">Earnings</th>
              </tr>
            </thead>
            <tbody>
              {analytics?.topRiders?.slice(0, 10).map((item: any, index: number) => (
                <tr key={item._id} className="border-b hover:bg-gray-50">
                  <td className="py-3 px-4">#{index + 1}</td>
                  <td className="py-3 px-4 font-medium">{item.rider?.name}</td>
                  <td className="py-3 px-4">{item.totalDeliveries}</td>
                  <td className="py-3 px-4">৳{(item.totalEarnings / 100).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Payment Methods */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-bold mb-4">Payment Method Distribution</h2>
        <div className="space-y-4">
          {analytics?.paymentMethods?.map((method: any) => (
            <div key={method._id} className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <span className="font-medium capitalize">{method._id}</span>
              </div>
              <div className="flex items-center gap-4">
                <span className="text-gray-600">{method.count} transactions</span>
                <span className="font-medium">৳{(method.totalAmount / 100).toFixed(2)}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
