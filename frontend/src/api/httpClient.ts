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
});

httpClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
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

    return Promise.reject(error);
  }
);

export default httpClient;
