import httpClient from './httpClient';
import type { TripPassengerResponse, TripPassengerDetailedResponse } from '../types/trip';

export const tripPassengerService = {

  async joinTrip(tripId: number): Promise<TripPassengerResponse> {
    const { data } = await httpClient.post(`/trips/${tripId}/join`);
    return data;
  },

  async getTripPassengers(tripId: number): Promise<TripPassengerDetailedResponse[]> {
    try {
      const { data } = await httpClient.get(`/trips/${tripId}/passengers`);
      return data;
    } catch (error: any) {
      if (error.response?.status === 404) return [];
      throw error;
    }
  },

  async getMyStatus(tripId: number): Promise<TripPassengerResponse | null> {
    try {
      const { data } = await httpClient.get(`/trips/${tripId}/my-status`);
      return data;
    } catch (error: any) {
      if (error.response?.status === 404) return null;
      throw error;
    }
  },

  async approvePassenger(tripId: number, passengerId: number): Promise<TripPassengerResponse> {
    const { data } = await httpClient.post(`/trips/${tripId}/passengers/${passengerId}/approve`);
    return data;
  },

  async rejectPassenger(tripId: number, passengerId: number): Promise<void> {
    await httpClient.delete(`/trips/${tripId}/passengers/${passengerId}/reject`);
  },

  async cancelMyRequest(tripId: number): Promise<void> {
    await httpClient.delete(`/trips/${tripId}/passengers/my-request`);
  },

  async pingPassenger(tripId: number, passengerId: number): Promise<void> {
    await httpClient.post(`/trips/${tripId}/passengers/${passengerId}/ping`);
  }
};
