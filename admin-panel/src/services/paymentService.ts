import api from './api';
import type { Payment, PaginatedResponse, PaginationParams, PaymentStatus, PaymentMethod } from '../types';

interface PaymentFilters extends PaginationParams {
  status?: PaymentStatus;
  method?: PaymentMethod;
  startDate?: string;
  endDate?: string;
  orderId?: string;
  userId?: string;
}

export const paymentService = {
  getAll: async (params?: PaymentFilters): Promise<PaginatedResponse<Payment>> => {
    const response = await api.get('/admin/payments', { params });
    return (response as { data: PaginatedResponse<Payment> }).data;
  },

  getById: async (id: string): Promise<Payment> => {
    const response = await api.get(`/admin/payments/${id}`);
    return (response as { data: Payment }).data;
  },

  refund: async (id: string, reason: string, amount?: number): Promise<Payment> => {
    const response = await api.post(`/admin/payments/${id}/refund`, { reason, amount });
    return (response as { data: Payment }).data;
  },

  getStats: async (startDate?: string, endDate?: string): Promise<unknown> => {
    const response = await api.get('/admin/payments/stats', {
      params: { startDate, endDate },
    });
    return response;
  },

  getRevenueByMethod: async (startDate?: string, endDate?: string): Promise<unknown> => {
    const response = await api.get('/admin/payments/revenue-by-method', {
      params: { startDate, endDate },
    });
    return response;
  },

  exportPayments: async (params?: PaymentFilters): Promise<Blob> => {
    const response = await api.get('/admin/payments/export', {
      params,
      responseType: 'blob',
    });
    return response as unknown as Blob;
  },

  getSettlements: async (params?: PaginationParams): Promise<unknown> => {
    const response = await api.get('/admin/payments/settlements', { params });
    return response;
  },

  processSettlement: async (restaurantId: string, amount: number): Promise<unknown> => {
    const response = await api.post('/admin/payments/settle', { restaurantId, amount });
    return response;
  },
};
