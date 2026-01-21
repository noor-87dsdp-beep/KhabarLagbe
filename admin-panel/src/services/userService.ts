import api from './api';
import type { User, PaginatedResponse, PaginationParams } from '../types';

interface UserFilters extends PaginationParams {
  role?: string;
  isVerified?: boolean;
  isBanned?: boolean;
}

export const userService = {
  getAll: async (params?: UserFilters): Promise<PaginatedResponse<User>> => {
    const response = await api.get('/admin/users', { params });
    return (response as { data: PaginatedResponse<User> }).data;
  },

  getById: async (id: string): Promise<User> => {
    const response = await api.get(`/admin/users/${id}`);
    return (response as { data: User }).data;
  },

  getOrderHistory: async (id: string, params?: PaginationParams): Promise<unknown> => {
    const response = await api.get(`/admin/users/${id}/orders`, { params });
    return response;
  },

  ban: async (id: string, reason: string): Promise<void> => {
    await api.patch(`/admin/users/${id}/ban`, { reason });
  },

  unban: async (id: string): Promise<void> => {
    await api.patch(`/admin/users/${id}/unban`);
  },

  update: async (id: string, data: Partial<User>): Promise<User> => {
    const response = await api.patch(`/admin/users/${id}`, data);
    return (response as { data: User }).data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/admin/users/${id}`);
  },

  exportUsers: async (params?: UserFilters): Promise<Blob> => {
    const response = await api.get('/admin/users/export', {
      params,
      responseType: 'blob',
    });
    return response as unknown as Blob;
  },
};
