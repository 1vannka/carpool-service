import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

const httpClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

httpClient.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken && config.headers) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
}, (error) => Promise.reject(error));

httpClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    const isAuthEndpoint = originalRequest.url?.includes('/auth/refresh') || originalRequest.url?.includes('/auth/logout');

    if (error.response?.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
      originalRequest._retry = true;
      const authStore = useAuthStore();

      if (authStore.refreshToken) {
        try {
          const newAccessToken = await authStore.refreshSession();
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return httpClient(originalRequest);
        } catch (refreshError) {
          authStore.logout();
          return Promise.reject(refreshError);
        }
      } else {
        authStore.logout();
      }
    }

    if (error.response?.status === 401 && isAuthEndpoint) {
      const authStore = useAuthStore();
      authStore.logout();
    }

    return Promise.reject(error);
  }
);

export default httpClient;
