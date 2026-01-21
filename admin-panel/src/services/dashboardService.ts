import api from './api';
import type { DashboardStats, RevenueData, TopRestaurant, TopRider } from '../types';

export const dashboardService = {
  getStats: async (): Promise<DashboardStats> => {
    const response = await api.get('/admin/dashboard/stats');
    return (response as { data: DashboardStats }).data;
  },

  getRevenueChart: async (days: number = 7): Promise<RevenueData[]> => {
    const response = await api.get('/admin/dashboard/revenue', { params: { days } });
    return (response as { data: RevenueData[] }).data;
  },

  getTopRestaurants: async (limit: number = 5): Promise<TopRestaurant[]> => {
    const response = await api.get('/admin/dashboard/top-restaurants', { params: { limit } });
    return (response as { data: TopRestaurant[] }).data;
  },

  getTopRiders: async (limit: number = 5): Promise<TopRider[]> => {
    const response = await api.get('/admin/dashboard/top-riders', { params: { limit } });
    return (response as { data: TopRider[] }).data;
  },

  getRecentOrders: async (limit: number = 10): Promise<unknown[]> => {
    const response = await api.get('/admin/dashboard/recent-orders', { params: { limit } });
    return (response as { data: unknown[] }).data;
  },

  getPendingApprovals: async (): Promise<{ restaurants: unknown[]; riders: unknown[] }> => {
    const response = await api.get('/admin/dashboard/pending-approvals');
    return (response as { data: { restaurants: unknown[]; riders: unknown[] } }).data;
  },
};
