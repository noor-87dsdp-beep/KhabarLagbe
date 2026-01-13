import { useState, useEffect } from 'react';
import { zoneService, Zone } from '../../services/zoneService';
import toast from 'react-hot-toast';

export default function ZoneManagement() {
  const [zones, setZones] = useState<Zone[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddDialog, setShowAddDialog] = useState(false);

  useEffect(() => {
    fetchZones();
  }, []);

  const fetchZones = async () => {
    try {
      setLoading(true);
      const response: any = await zoneService.getAll();
      setZones(response.data || []);
    } catch (error) {
      console.error('Failed to fetch zones:', error);
      toast.error('Failed to load zones');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActive = async (id: string) => {
    try {
      await zoneService.toggleActive(id);
      toast.success('Zone status updated');
      fetchZones();
    } catch (error) {
      console.error('Failed to toggle zone:', error);
      toast.error('Failed to update zone status');
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this zone?')) return;
    
    try {
      await zoneService.delete(id);
      toast.success('Zone deleted successfully');
      fetchZones();
    } catch (error: any) {
      console.error('Failed to delete zone:', error);
      toast.error(error.response?.data?.message || 'Failed to delete zone');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading zones...</div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Zone Management</h1>
        <button
          onClick={() => setShowAddDialog(true)}
          className="bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-orange-600"
        >
          + Add New Zone
        </button>
      </div>

      {/* Zone List */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {zones.map((zone) => (
          <div
            key={zone._id}
            className="bg-white rounded-lg shadow-md p-6 border border-gray-200"
          >
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-xl font-bold">{zone.name}</h3>
                <p className="text-gray-600">{zone.nameBn}</p>
              </div>
              <span
                className={`px-3 py-1 rounded-full text-sm ${
                  zone.isActive
                    ? 'bg-green-100 text-green-800'
                    : 'bg-red-100 text-red-800'
                }`}
              >
                {zone.isActive ? 'Active' : 'Inactive'}
              </span>
            </div>

            <div className="space-y-2 text-sm text-gray-700 mb-4">
              <div className="flex justify-between">
                <span>Delivery Fee:</span>
                <span className="font-semibold">
                  ৳{(zone.deliveryFee / 100).toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span>Per KM Fee:</span>
                <span className="font-semibold">
                  ৳{(zone.perKmFee / 100).toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span>Estimated Time:</span>
                <span className="font-semibold">{zone.estimatedTime}</span>
              </div>
              <div className="flex justify-between">
                <span>Restaurants:</span>
                <span className="font-semibold">{zone.restaurantCount}</span>
              </div>
              <div className="flex justify-between">
                <span>Riders:</span>
                <span className="font-semibold">{zone.riderCount}</span>
              </div>
            </div>

            <div className="flex gap-2">
              <button
                onClick={() => handleToggleActive(zone._id)}
                className="flex-1 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
              >
                {zone.isActive ? 'Deactivate' : 'Activate'}
              </button>
              <button
                onClick={() => handleDelete(zone._id)}
                className="flex-1 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {zones.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 text-lg">
            No zones found. Create your first zone to get started.
          </p>
        </div>
      )}

      {/* Add Zone Dialog - Simplified for now */}
      {showAddDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full">
            <h2 className="text-2xl font-bold mb-4">Add New Zone</h2>
            <p className="text-gray-600 mb-4">
              Zone creation with Mapbox integration requires additional setup.
              For now, zones can be created via API.
            </p>
            <button
              onClick={() => setShowAddDialog(false)}
              className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
