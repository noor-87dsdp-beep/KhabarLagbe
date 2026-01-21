import api from './api';
import type { PromoCode, PaginatedResponse, PaginationParams } from '../types';

interface PromoFilters extends PaginationParams {
  isActive?: boolean;
  type?: 'percentage' | 'fixed';
}

interface CreatePromoData {
  code: string;
  description: string;
  type: 'percentage' | 'fixed';
  value: number;
  minOrderAmount?: number;
  maxDiscount?: number;
  usageLimit?: {
    total?: number;
    perUser?: number;
  };
  validFrom: string;
  validUntil: string;
  applicableRestaurants?: string[];
  applicableCategories?: string[];
}

export const promoService = {
  getAll: async (params?: PromoFilters): Promise<PaginatedResponse<PromoCode>> => {
    const response = await api.get('/admin/promo-codes', { params });
    return (response as { data: PaginatedResponse<PromoCode> }).data;
  },

  getById: async (id: string): Promise<PromoCode> => {
    const response = await api.get(`/admin/promo-codes/${id}`);
    return (response as { data: PromoCode }).data;
  },

  getByCode: async (code: string): Promise<PromoCode> => {
    const response = await api.get(`/admin/promo-codes/code/${code}`);
    return (response as { data: PromoCode }).data;
  },

  create: async (data: CreatePromoData): Promise<PromoCode> => {
    const response = await api.post('/admin/promo-codes', data);
    return (response as { data: PromoCode }).data;
  },

  update: async (id: string, data: Partial<CreatePromoData>): Promise<PromoCode> => {
    const response = await api.patch(`/admin/promo-codes/${id}`, data);
    return (response as { data: PromoCode }).data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/admin/promo-codes/${id}`);
  },

  toggleStatus: async (id: string): Promise<PromoCode> => {
    const response = await api.patch(`/admin/promo-codes/${id}/toggle-status`);
    return (response as { data: PromoCode }).data;
  },

  getUsageStats: async (id: string): Promise<unknown> => {
    const response = await api.get(`/admin/promo-codes/${id}/usage`);
    return response;
  },

  validateCode: async (code: string, orderAmount: number): Promise<unknown> => {
    const response = await api.post('/admin/promo-codes/validate', { code, orderAmount });
    return response;
  },
};
