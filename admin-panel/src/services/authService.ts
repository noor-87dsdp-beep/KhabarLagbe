import api from './api';
import type { LoginCredentials, AuthResponse, Admin } from '../types';

export const authService = {
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    const response = await api.post('/admin/login', credentials);
    return response as unknown as AuthResponse;
  },

  logout: async (): Promise<void> => {
    await api.post('/admin/logout');
  },

  getCurrentAdmin: async (): Promise<Admin> => {
    const response = await api.get('/admin/me');
    return response as unknown as Admin;
  },

  updateProfile: async (data: Partial<Admin>): Promise<Admin> => {
    const response = await api.patch('/admin/profile', data);
    return response as unknown as Admin;
  },

  changePassword: async (data: { currentPassword: string; newPassword: string }): Promise<void> => {
    await api.post('/admin/change-password', data);
  },

  forgotPassword: async (email: string): Promise<void> => {
    await api.post('/admin/forgot-password', { email });
  },

  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    await api.post('/admin/reset-password', { token, newPassword });
  },
};
