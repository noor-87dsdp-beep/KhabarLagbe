import api from './api';

interface AnalyticsParams {
  startDate?: string;
  endDate?: string;
  groupBy?: 'day' | 'week' | 'month';
}

export const analyticsService = {
  getOverview: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/overview', { params });
    return response;
  },

  getRevenueAnalytics: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/revenue', { params });
    return response;
  },

  getOrderAnalytics: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/orders', { params });
    return response;
  },

  getUserGrowth: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/user-growth', { params });
    return response;
  },

  getRestaurantPerformance: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/restaurant-performance', { params });
    return response;
  },

  getRiderPerformance: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/rider-performance', { params });
    return response;
  },

  getPopularItems: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/popular-items', { params });
    return response;
  },

  getPaymentMethodDistribution: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/payment-methods', { params });
    return response;
  },

  getZoneAnalytics: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/zones', { params });
    return response;
  },

  getCancellationReasons: async (params?: AnalyticsParams): Promise<unknown> => {
    const response = await api.get('/admin/analytics/cancellation-reasons', { params });
    return response;
  },

  exportReport: async (type: string, params?: AnalyticsParams): Promise<Blob> => {
    const response = await api.get(`/admin/analytics/export/${type}`, {
      params,
      responseType: 'blob',
    });
    return response as unknown as Blob;
  },

  getCustomReport: async (config: unknown): Promise<unknown> => {
    const response = await api.post('/admin/analytics/custom-report', config);
    return response;
  },
};
