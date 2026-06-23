import httpClient from './httpClient';
import type {UpdateProfileRequest, UserProfileResponse } from '../types/user';

export const profileService = {
  async getProfile(): Promise<UserProfileResponse> {
    const { data } = await httpClient.get('/profile');
    return data;
  },

  async updateProfile(request: UpdateProfileRequest): Promise<UserProfileResponse> {
    const { data } = await httpClient.put('/profile', request);
    return data;
  }
};
