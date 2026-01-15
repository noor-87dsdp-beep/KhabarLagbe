import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import api from '../../services/api';

interface PromoCode {
  _id: string;
  code: string;
  description: string;
  type: 'percentage' | 'fixed';
  value: number;
  minOrderAmount: number;
  maxDiscount?: number;
  usageCount: number;
  usageLimit?: { total?: number; perUser: number };
  validFrom: string;
  validUntil: string;
  isActive: boolean;
}

export default function PromoCodeManagement() {
  const [promoCodes, setPromoCodes] = useState<PromoCode[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [formData, setFormData] = useState({
    code: '',
    description: '',
    type: 'percentage',
    value: 0,
    minOrderAmount: 0,
    maxDiscount: '',
    validFrom: '',
    validUntil: '',
  });

  useEffect(() => {
    fetchPromoCodes();
  }, []);

  const fetchPromoCodes = async () => {
    try {
      setLoading(true);
      const response = await api.get('/promo-codes');
      setPromoCodes(response.data.data.promoCodes || []);
    } catch (error) {
      console.error('Failed to fetch promo codes:', error);
      toast.error('Failed to load promo codes');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const payload = {
        ...formData,
        code: formData.code.toUpperCase(),
        maxDiscount: formData.maxDiscount ? Number(formData.maxDiscount) * 100 : undefined,
        value: Number(formData.value) * (formData.type === 'fixed' ? 100 : 1),
        minOrderAmount: Number(formData.minOrderAmount) * 100,
      };

      await api.post('/promo-codes', payload);
      toast.success('Promo code created successfully');
      setShowCreateDialog(false);
      setFormData({
        code: '',
        description: '',
        type: 'percentage',
        value: 0,
        minOrderAmount: 0,
        maxDiscount: '',
        validFrom: '',
        validUntil: '',
      });
      fetchPromoCodes();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to create promo code');
    }
  };

  const handleToggleStatus = async (id: string) => {
    try {
      await api.patch(`/promo-codes/${id}/toggle-status`);
      toast.success('Status updated');
      fetchPromoCodes();
    } catch (error) {
      toast.error('Failed to update status');
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this promo code?')) return;
    
    try {
      await api.delete(`/promo-codes/${id}`);
      toast.success('Promo code deleted');
      fetchPromoCodes();
    } catch (error) {
      toast.error('Failed to delete promo code');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-xl">Loading promo codes...</div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Promo Code Management</h1>
        <button
          onClick={() => setShowCreateDialog(true)}
          className="bg-orange-600 text-white px-6 py-2 rounded-lg hover:bg-orange-700"
        >
          Create Promo Code
        </button>
      </div>

      {/* Promo Codes Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {promoCodes.map((promo) => (
          <div key={promo._id} className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-xl font-bold text-orange-600">{promo.code}</h3>
                <p className="text-sm text-gray-600 mt-1">{promo.description}</p>
              </div>
              <span className={`px-2 py-1 text-xs rounded-full ${
                promo.isActive ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
              }`}>
                {promo.isActive ? 'Active' : 'Inactive'}
              </span>
            </div>

            <div className="space-y-2 mb-4">
              <div className="text-sm">
                <span className="text-gray-600">Discount:</span>
                <span className="ml-2 font-medium">
                  {promo.type === 'percentage' 
                    ? `${promo.value}%` 
                    : `৳${(promo.value / 100).toFixed(0)}`}
                </span>
              </div>
              
              {promo.minOrderAmount > 0 && (
                <div className="text-sm">
                  <span className="text-gray-600">Min Order:</span>
                  <span className="ml-2">৳{(promo.minOrderAmount / 100).toFixed(0)}</span>
                </div>
              )}
              
              {promo.maxDiscount && (
                <div className="text-sm">
                  <span className="text-gray-600">Max Discount:</span>
                  <span className="ml-2">৳{(promo.maxDiscount / 100).toFixed(0)}</span>
                </div>
              )}

              <div className="text-sm">
                <span className="text-gray-600">Used:</span>
                <span className="ml-2">{promo.usageCount} times</span>
              </div>

              <div className="text-sm">
                <span className="text-gray-600">Valid Until:</span>
                <span className="ml-2">{new Date(promo.validUntil).toLocaleDateString()}</span>
              </div>
            </div>

            <div className="flex gap-2">
              <button
                onClick={() => handleToggleStatus(promo._id)}
                className={`flex-1 px-4 py-2 rounded-lg ${
                  promo.isActive 
                    ? 'bg-gray-200 text-gray-800 hover:bg-gray-300' 
                    : 'bg-green-600 text-white hover:bg-green-700'
                }`}
              >
                {promo.isActive ? 'Deactivate' : 'Activate'}
              </button>
              <button
                onClick={() => handleDelete(promo._id)}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Create Dialog */}
      {showCreateDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full max-h-[90vh] overflow-y-auto">
            <h2 className="text-2xl font-bold mb-4">Create Promo Code</h2>
            
            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">Code *</label>
                <input
                  type="text"
                  value={formData.code}
                  onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Description *</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  rows={2}
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Type *</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value as 'percentage' | 'fixed' })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                >
                  <option value="percentage">Percentage</option>
                  <option value="fixed">Fixed Amount</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Value * {formData.type === 'percentage' ? '(%)' : '(৳)'}
                </label>
                <input
                  type="number"
                  value={formData.value}
                  onChange={(e) => setFormData({ ...formData, value: Number(e.target.value) })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  min="1"
                  max={formData.type === 'percentage' ? 100 : undefined}
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Min Order Amount (৳)</label>
                <input
                  type="number"
                  value={formData.minOrderAmount}
                  onChange={(e) => setFormData({ ...formData, minOrderAmount: Number(e.target.value) })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  min="0"
                />
              </div>

              {formData.type === 'percentage' && (
                <div>
                  <label className="block text-sm font-medium mb-1">Max Discount (৳)</label>
                  <input
                    type="number"
                    value={formData.maxDiscount}
                    onChange={(e) => setFormData({ ...formData, maxDiscount: e.target.value })}
                    className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                    min="0"
                  />
                </div>
              )}

              <div>
                <label className="block text-sm font-medium mb-1">Valid From *</label>
                <input
                  type="datetime-local"
                  value={formData.validFrom}
                  onChange={(e) => setFormData({ ...formData, validFrom: e.target.value })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Valid Until *</label>
                <input
                  type="datetime-local"
                  value={formData.validUntil}
                  onChange={(e) => setFormData({ ...formData, validUntil: e.target.value })}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500"
                  required
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="flex-1 bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700"
                >
                  Create
                </button>
                <button
                  type="button"
                  onClick={() => setShowCreateDialog(false)}
                  className="flex-1 bg-gray-200 px-4 py-2 rounded-lg hover:bg-gray-300"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
