import api from './api';

export interface Zone {
  _id: string;
  name: string;
  nameBn: string;
  polygon: {
    type: string;
    coordinates: number[][][];
  };
  center: {
    type: string;
    coordinates: number[];
  };
  deliveryFee: number;
  perKmFee: number;
  estimatedTime: string;
  isActive: boolean;
  restaurantCount: number;
  riderCount: number;
  createdAt: string;
  updatedAt: string;
}

export const zoneService = {
  getAll: () => api.get('/zones/admin/all'),
  
  getActive: () => api.get('/zones'),
  
  getById: (id: string) => api.get(`/zones/${id}`),
  
  create: (data: Partial<Zone>) => api.post('/zones', data),
  
  update: (id: string, data: Partial<Zone>) => 
    api.put(`/zones/${id}`, data),
  
  toggleActive: (id: string) => 
    api.patch(`/zones/${id}/toggle`),
  
  delete: (id: string) => api.delete(`/zones/${id}`),
  
  getRestaurants: (id: string) => 
    api.get(`/zones/${id}/restaurants`),
};
