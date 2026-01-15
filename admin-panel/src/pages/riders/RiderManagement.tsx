import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '../../services/api';

interface Rider {
  _id: string;
  name: string;
  phone: string;
  email?: string;
  vehicleType: string;
  vehicleNumber?: string;
  zone: string;
  status: string;
  rating: number;
  totalDeliveries: number;
  isApproved: boolean;
  createdAt: string;
}

export default function RiderManagement() {
  const [riders, setRiders] = useState<Rider[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'all' | 'pending'>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');

  useEffect(() => {
    fetchRiders();
  }, [activeTab, statusFilter]);

  const fetchRiders = async () => {
    try {
      setLoading(true);
      const params: any = {};
      
      if (activeTab === 'pending') {
        params.isApproved = 'false';
      }
      
      if (statusFilter !== 'all') {
        params.status = statusFilter;
      }

      const response = await api.get('/riders', { params });
      setRiders(response.data.data.riders || []);
    } catch (error) {
      console.error('Failed to fetch riders:', error);
      toast.error('Failed to load riders');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (riderId: string) => {
    try {
      await api.patch(`/riders/${riderId}/approve`);
      toast.success('Rider approved successfully');
      fetchRiders();
    } catch (error) {
      console.error('Failed to approve rider:', error);
      toast.error('Failed to approve rider');
    }
  };

  const handleSuspend = async (riderId: string, reason: string = 'Violation of terms') => {
    try {
      await api.patch(`/riders/${riderId}/suspend`, { reason });
      toast.success('Rider suspended');
      fetchRiders();
    } catch (error) {
      console.error('Failed to suspend rider:', error);
      toast.error('Failed to suspend rider');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading riders...</div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">Rider Management</h1>

      {/* Tabs */}
      <div className="flex gap-4 mb-6 border-b">
        <button
          onClick={() => setActiveTab('all')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'all'
              ? 'border-b-2 border-orange-500 text-orange-600'
              : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          All Riders ({riders.length})
        </button>
        <button
          onClick={() => setActiveTab('pending')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'pending'
              ? 'border-b-2 border-orange-500 text-orange-600'
              : 'text-gray-600 hover:text-gray-900'
          }`}
        >
          Pending Approval
        </button>
      </div>

      {/* Filters */}
      {activeTab === 'all' && (
        <div className="bg-white rounded-lg shadow p-4 mb-6">
          <div className="flex gap-4">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
            >
              <option value="all">All Status</option>
              <option value="available">Available</option>
              <option value="busy">Busy</option>
              <option value="offline">Offline</option>
              <option value="on_break">On Break</option>
            </select>
          </div>
        </div>
      )}

      {/* Riders Grid/Table */}
      {activeTab === 'pending' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {riders.map((rider) => (
            <div key={rider._id} className="bg-white rounded-lg shadow p-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold">{rider.name}</h3>
                  <p className="text-sm text-gray-600">{rider.phone}</p>
                </div>
                <span className="px-2 py-1 bg-yellow-100 text-yellow-800 text-xs rounded-full">
                  Pending
                </span>
              </div>

              <div className="space-y-2 mb-4">
                <div className="text-sm">
                  <span className="text-gray-600">Vehicle:</span>
                  <span className="ml-2 font-medium">{rider.vehicleType}</span>
                </div>
                {rider.vehicleNumber && (
                  <div className="text-sm">
                    <span className="text-gray-600">Vehicle #:</span>
                    <span className="ml-2 font-medium">{rider.vehicleNumber}</span>
                  </div>
                )}
                <div className="text-sm">
                  <span className="text-gray-600">Zone:</span>
                  <span className="ml-2 font-medium">{rider.zone}</span>
                </div>
                <div className="text-sm">
                  <span className="text-gray-600">Applied:</span>
                  <span className="ml-2">{new Date(rider.createdAt).toLocaleDateString()}</span>
                </div>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={() => handleApprove(rider._id)}
                  className="flex-1 bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
                >
                  Approve
                </button>
                <button
                  onClick={() => handleSuspend(rider._id, 'Application rejected')}
                  className="flex-1 bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700"
                >
                  Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Rider
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Vehicle
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Zone
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Performance
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {riders.map((rider) => (
                <tr key={rider._id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{rider.name}</div>
                    <div className="text-sm text-gray-500">{rider.phone}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm text-gray-900">{rider.vehicleType}</div>
                    {rider.vehicleNumber && (
                      <div className="text-sm text-gray-500">{rider.vehicleNumber}</div>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {rider.zone}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs rounded-full ${
                      rider.status === 'available' ? 'bg-green-100 text-green-800' :
                      rider.status === 'busy' ? 'bg-blue-100 text-blue-800' :
                      rider.status === 'on_break' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {rider.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm">
                      <div>⭐ {rider.rating.toFixed(1)}</div>
                      <div className="text-gray-500">{rider.totalDeliveries} deliveries</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button className="text-orange-600 hover:text-orange-900 mr-3">
                      View
                    </button>
                    <button
                      onClick={() => handleSuspend(rider._id)}
                      className="text-red-600 hover:text-red-900"
                    >
                      Suspend
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
