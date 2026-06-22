import httpClient from './httpClient';
import type {RideRequestCreateRequest,RideRequestResponse } from '../types/ride.ts'

export const rideRequestService = {
  async createRideRequest(request: RideRequestCreateRequest): Promise<RideRequestResponse> {
    const { data } = await httpClient.post('/ride-requests', request);
    return data;
  },

  async getMyActiveRequest(): Promise<RideRequestResponse | null> {
    try {
      const { data } = await httpClient.get('/ride-requests/my-active');
      return data;
    } catch (error: any) {
      if (error.response?.status === 404) return null;
      throw error;
    }
  },

  async cancelRideRequest(id: number): Promise<void> {
    await httpClient.delete(`/ride-requests/${id}`);
  }
};
