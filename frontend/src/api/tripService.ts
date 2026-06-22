import httpClient from './httpClient';
import type {TripCreateRequest,TripResponse} from '../types/trip.ts';

export const tripService = {
  async createTrip(request: TripCreateRequest): Promise<TripResponse> {
    const { data } = await httpClient.post('/trips', request);
    return data;
  },

  async getActiveTrip(): Promise<TripResponse | null> {
    try {
      const { data } = await httpClient.get('/trips/my-active');
      return data;
    } catch (error: any) {
      if (error.response?.status === 404) return null;
      throw error;
    }
  },

  async updateTrip(id: number, request: TripCreateRequest): Promise<TripResponse> {
    const { data } = await httpClient.put(`/trips/${id}`, request);
    return data;
  },

  async cancelTrip(id: number): Promise<void> {
    await httpClient.delete(`/trips/${id}`);
  }
};
