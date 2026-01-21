import api from './api';
import type { Order, PaginatedResponse, PaginationParams, OrderStatus } from '../types';

interface OrderFilters extends PaginationParams {
  status?: OrderStatus;
  startDate?: string;
  endDate?: string;
  restaurantId?: string;
  riderId?: string;
  userId?: string;
  paymentStatus?: string;
}

export const orderService = {
  getAll: async (params?: OrderFilters): Promise<PaginatedResponse<Order>> => {
    const response = await api.get('/admin/orders', { params });
    return (response as { data: PaginatedResponse<Order> }).data;
  },

  getById: async (id: string): Promise<Order> => {
    const response = await api.get(`/admin/orders/${id}`);
    return (response as { data: Order }).data;
  },

  getByOrderNumber: async (orderNumber: string): Promise<Order> => {
    const response = await api.get(`/admin/orders/number/${orderNumber}`);
    return (response as { data: Order }).data;
  },

  updateStatus: async (id: string, status: OrderStatus, note?: string): Promise<Order> => {
    const response = await api.patch(`/admin/orders/${id}/status`, { status, note });
    return (response as { data: Order }).data;
  },

  cancel: async (id: string, reason: string): Promise<Order> => {
    const response = await api.patch(`/admin/orders/${id}/cancel`, { reason });
    return (response as { data: Order }).data;
  },

  assignRider: async (orderId: string, riderId: string): Promise<Order> => {
    const response = await api.patch(`/admin/orders/${orderId}/assign-rider`, { riderId });
    return (response as { data: Order }).data;
  },

  getTimeline: async (id: string): Promise<unknown> => {
    const response = await api.get(`/admin/orders/${id}/timeline`);
    return response;
  },

  getStats: async (startDate?: string, endDate?: string): Promise<unknown> => {
    const response = await api.get('/admin/orders/stats', {
      params: { startDate, endDate },
    });
    return response;
  },

  exportOrders: async (params?: OrderFilters): Promise<Blob> => {
    const response = await api.get('/admin/orders/export', {
      params,
      responseType: 'blob',
    });
    return response as unknown as Blob;
  },
};
