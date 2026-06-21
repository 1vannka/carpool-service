import { defineStore } from 'pinia';
import httpClient from '../api/httpClient';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  roles?: string[];
  exp: number;
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem('access_token') || null,
    refreshToken: localStorage.getItem('refresh_token') || null,
    userRole: localStorage.getItem('user_role') || null,
    userEmail: localStorage.getItem('user_email') || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.accessToken,
    isAdmin: (state) => state.userRole === 'ROLE_ADMIN' || state.userRole === 'ADMIN',
  },
  actions: {
    _saveTokens(access: string, refresh: string) {
      this.accessToken = access;
      this.refreshToken = refresh;
      localStorage.setItem('access_token', access);
      localStorage.setItem('refresh_token', refresh);

      try {
        const decoded = jwtDecode<any>(access);

        this.userEmail = decoded.sub;

        const roles = decoded.roles || decoded.authorities || decoded.role;

        if (Array.isArray(roles) && roles.length > 0) {
          this.userRole = typeof roles[0] === 'string' ? roles[0] : roles[0].authority || null;
        } else if (typeof roles === 'string') {
          this.userRole = roles;
        }

        localStorage.setItem('user_email', this.userEmail as string);
        if (this.userRole) localStorage.setItem('user_role', this.userRole);

      } catch (e) {
        console.error("Ошибка парсинга токена", e);
      }
    },

    async login(credentials: { email: string; password: string }) {
      const response = await httpClient.post('/auth/login', credentials);
      this._saveTokens(response.data.accessToken, response.data.refreshToken);
    },

    async refreshSession() {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/refresh', {
          refreshToken: this.refreshToken
        });
        this._saveTokens(response.data.accessToken, response.data.refreshToken);
        return this.accessToken;
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    logout() {
      if (this.refreshToken) {
        httpClient.post('/auth/logout', { refreshToken: this.refreshToken }).catch(console.error);
      }

      this.accessToken = null;
      this.refreshToken = null;
      this.userRole = null;
      this.userEmail = null;
      localStorage.clear();
      window.location.href = '/login';
    }
  }
});
