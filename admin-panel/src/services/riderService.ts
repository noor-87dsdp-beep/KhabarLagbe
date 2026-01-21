import api from './api';
import type { Rider, PaginatedResponse, PaginationParams } from '../types';

interface RiderFilters extends PaginationParams {
  status?: string;
  isApproved?: boolean;
  zone?: string;
  vehicleType?: string;
}

export const riderService = {
  getAll: async (params?: RiderFilters): Promise<PaginatedResponse<Rider>> => {
    const response = await api.get('/admin/riders', { params });
    return (response as { data: PaginatedResponse<Rider> }).data;
  },

  getById: async (id: string): Promise<Rider> => {
    const response = await api.get(`/admin/riders/${id}`);
    return (response as { data: Rider }).data;
  },

  getPending: async (): Promise<Rider[]> => {
    const response = await api.get('/admin/riders/pending');
    return (response as { data: Rider[] }).data;
  },

  approve: async (id: string): Promise<void> => {
    await api.patch(`/admin/riders/${id}/approve`);
  },

  reject: async (id: string, reason: string): Promise<void> => {
    await api.patch(`/admin/riders/${id}/reject`, { reason });
  },

  suspend: async (id: string, reason: string): Promise<void> => {
    await api.patch(`/admin/riders/${id}/suspend`, { reason });
  },

  activate: async (id: string): Promise<void> => {
    await api.patch(`/admin/riders/${id}/activate`);
  },

  verifyDocument: async (riderId: string, documentType: string): Promise<void> => {
    await api.patch(`/admin/riders/${riderId}/verify-document`, { documentType });
  },

  getDeliveryHistory: async (id: string, params?: PaginationParams): Promise<unknown> => {
    const response = await api.get(`/admin/riders/${id}/deliveries`, { params });
    return response;
  },

  getEarnings: async (id: string, startDate?: string, endDate?: string): Promise<unknown> => {
    const response = await api.get(`/admin/riders/${id}/earnings`, {
      params: { startDate, endDate },
    });
    return response;
  },

  assignZone: async (id: string, zoneId: string): Promise<void> => {
    await api.patch(`/admin/riders/${id}/assign-zone`, { zoneId });
  },
};
