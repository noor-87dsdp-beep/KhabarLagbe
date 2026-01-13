import { useState, useEffect } from 'react';
import { restaurantService, Restaurant } from '../../services/restaurantService';
import toast from 'react-hot-toast';

export default function RestaurantManagement() {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [pendingRestaurants, setPendingRestaurants] = useState<Restaurant[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'all' | 'pending'>('all');
  const [showRejectDialog, setShowRejectDialog] = useState(false);
  const [selectedRestaurant, setSelectedRestaurant] = useState<string | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');

  useEffect(() => {
    fetchRestaurants();
    fetchPending();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setLoading(true);
      const response: any = await restaurantService.getAll();
      setRestaurants(response.data?.restaurants || []);
    } catch (error) {
      console.error('Failed to fetch restaurants:', error);
      toast.error('Failed to load restaurants');
    } finally {
      setLoading(false);
    }
  };

  const fetchPending = async () => {
    try {
      const response: any = await restaurantService.getPending();
      setPendingRestaurants(response.data || []);
    } catch (error) {
      console.error('Failed to fetch pending restaurants:', error);
    }
  };

  const handleApprove = async (id: string) => {
    try {
      await restaurantService.approve(id);
      toast.success('Restaurant approved successfully');
      fetchRestaurants();
      fetchPending();
    } catch (error) {
      console.error('Failed to approve restaurant:', error);
      toast.error('Failed to approve restaurant');
    }
  };

  const handleReject = async () => {
    if (!selectedRestaurant || !rejectionReason) {
      toast.error('Please provide a rejection reason');
      return;
    }

    try {
      await restaurantService.reject(selectedRestaurant, rejectionReason);
      toast.success('Restaurant rejected');
      setShowRejectDialog(false);
      setSelectedRestaurant(null);
      setRejectionReason('');
      fetchRestaurants();
      fetchPending();
    } catch (error) {
      console.error('Failed to reject restaurant:', error);
      toast.error('Failed to reject restaurant');
    }
  };

  const handleSuspend = async (id: string) => {
    try {
      await restaurantService.suspend(id);
      toast.success('Restaurant status updated');
      fetchRestaurants();
    } catch (error) {
      console.error('Failed to suspend restaurant:', error);
      toast.error('Failed to update restaurant status');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading restaurants...</div>
      </div>
    );
  }

  const displayRestaurants = activeTab === 'pending' ? pendingRestaurants : restaurants;

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">Restaurant Management</h1>

      {/* Tabs */}
      <div className="flex gap-4 mb-6 border-b">
        <button
          onClick={() => setActiveTab('all')}
          className={`px-6 py-3 font-semibold ${
            activeTab === 'all'
              ? 'border-b-2 border-orange-500 text-orange-500'
              : 'text-gray-600'
          }`}
        >
          All Restaurants ({restaurants.length})
        </button>
        <button
          onClick={() => setActiveTab('pending')}
          className={`px-6 py-3 font-semibold ${
            activeTab === 'pending'
              ? 'border-b-2 border-orange-500 text-orange-500'
              : 'text-gray-600'
          }`}
        >
          Pending Approval ({pendingRestaurants.length})
        </button>
      </div>

      {/* Restaurant List */}
      <div className="bg-white rounded-lg shadow">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Restaurant
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Location
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Contact
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Stats
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Status
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {displayRestaurants.map((restaurant) => (
              <tr key={restaurant._id}>
                <td className="px-6 py-4">
                  <div>
                    <div className="font-semibold">{restaurant.name}</div>
                    <div className="text-sm text-gray-500">{restaurant.nameBn}</div>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm">
                    <div>{restaurant.address.area}</div>
                    <div className="text-gray-500">{restaurant.address.thana}</div>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm">
                    <div>{restaurant.phone}</div>
                    {restaurant.email && (
                      <div className="text-gray-500">{restaurant.email}</div>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm">
                    <div>⭐ {restaurant.rating.toFixed(1)} ({restaurant.totalReviews})</div>
                    <div className="text-gray-500">
                      {restaurant.totalOrders} orders
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="space-y-1">
                    <span
                      className={`inline-block px-2 py-1 rounded-full text-xs ${
                        restaurant.approvalStatus === 'approved'
                          ? 'bg-green-100 text-green-800'
                          : restaurant.approvalStatus === 'rejected'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-yellow-100 text-yellow-800'
                      }`}
                    >
                      {restaurant.approvalStatus}
                    </span>
                    {restaurant.isActive && (
                      <span className="inline-block ml-2 px-2 py-1 rounded-full text-xs bg-blue-100 text-blue-800">
                        {restaurant.isOpen ? 'Open' : 'Closed'}
                      </span>
                    )}
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="flex gap-2">
                    {restaurant.approvalStatus === 'pending' && (
                      <>
                        <button
                          onClick={() => handleApprove(restaurant._id)}
                          className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 text-sm"
                        >
                          Approve
                        </button>
                        <button
                          onClick={() => {
                            setSelectedRestaurant(restaurant._id);
                            setShowRejectDialog(true);
                          }}
                          className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm"
                        >
                          Reject
                        </button>
                      </>
                    )}
                    {restaurant.approvalStatus === 'approved' && (
                      <button
                        onClick={() => handleSuspend(restaurant._id)}
                        className={`px-3 py-1 rounded text-sm ${
                          restaurant.isActive
                            ? 'bg-yellow-500 hover:bg-yellow-600'
                            : 'bg-green-500 hover:bg-green-600'
                        } text-white`}
                      >
                        {restaurant.isActive ? 'Suspend' : 'Activate'}
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {displayRestaurants.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">
            {activeTab === 'pending'
              ? 'No pending restaurant approvals'
              : 'No restaurants found'}
          </p>
        </div>
      )}

      {/* Reject Dialog */}
      {showRejectDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-2xl font-bold mb-4">Reject Restaurant</h2>
            <p className="text-gray-600 mb-4">
              Please provide a reason for rejecting this restaurant:
            </p>
            <textarea
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              className="w-full border rounded p-2 mb-4 h-32"
              placeholder="Enter rejection reason..."
            />
            <div className="flex gap-2">
              <button
                onClick={handleReject}
                className="flex-1 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
              >
                Reject
              </button>
              <button
                onClick={() => {
                  setShowRejectDialog(false);
                  setSelectedRestaurant(null);
                  setRejectionReason('');
                }}
                className="flex-1 bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
