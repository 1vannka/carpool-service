import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

let isRefreshing = false;
let failedQueue: Array<{ resolve: (value?: unknown) => void, reject: (reason?: any) => void }> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

httpClient.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken && config.headers) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
}, (error) => Promise.reject(error));

httpClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const isAuthEndpoint = originalRequest.url?.includes('/auth/refresh') || originalRequest.url?.includes('/auth/logout');

    const status = error.response?.status;

    if ((status === 401 || status === 403) && !originalRequest._retry && !isAuthEndpoint) {
      if (isRefreshing) {
        return new Promise(function(resolve, reject) {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest._retry = true;
          originalRequest.headers.Authorization = 'Bearer ' + token;
          return httpClient(originalRequest);
        }).catch(err => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;
      const authStore = useAuthStore();

      if (authStore.refreshToken) {
        try {
          const newAccessToken = await authStore.refreshSession();
          processQueue(null, newAccessToken);
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return httpClient(originalRequest);
        } catch (refreshError) {
          processQueue(refreshError, null);

          authStore.logout();
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      } else {
        authStore.logout();
        return Promise.reject(error);
      }
    }

    if ((status === 401 || status === 403) && isAuthEndpoint) {
      const authStore = useAuthStore();
      authStore.logout();
    }

    return Promise.reject(error);
  }
);

export default httpClient;
