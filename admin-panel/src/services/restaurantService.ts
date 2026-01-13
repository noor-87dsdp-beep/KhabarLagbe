import api from './api';

export interface Restaurant {
  _id: string;
  name: string;
  nameBn: string;
  phone: string;
  email: string;
  location: {
    type: string;
    coordinates: number[];
  };
  address: {
    street: string;
    area: string;
    thana: string;
    district: string;
  };
  rating: number;
  totalReviews: number;
  totalOrders: number;
  isActive: boolean;
  isOpen: boolean;
  approvalStatus: 'pending' | 'approved' | 'rejected';
  rejectionReason?: string;
  createdAt: string;
}

export const restaurantService = {
  getAll: (params?: { page?: number; limit?: number; status?: string }) => 
    api.get('/restaurants/admin/all', { params }),
  
  getPending: () => api.get('/restaurants/admin/pending'),
  
  approve: (id: string) => 
    api.patch(`/restaurants/admin/${id}/approve`),
  
  reject: (id: string, reason: string) => 
    api.patch(`/restaurants/admin/${id}/reject`, { reason }),
  
  suspend: (id: string) => 
    api.patch(`/restaurants/admin/${id}/suspend`),
};
