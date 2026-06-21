import httpClient from './httpClient';
import type { OfficeResponse, OfficeCreateRequest, OfficeUpdateRequest } from '../types/office';

export const officeService = {
  async getOffices(city?: string): Promise<OfficeResponse[]> {
    const params = city ? { city } : {};
    const response = await httpClient.get<OfficeResponse[]>('/offices', { params });
    return response.data;
  },

  async createOffice(data: OfficeCreateRequest): Promise<OfficeResponse> {
    const response = await httpClient.post<OfficeResponse>('/offices', data);
    return response.data;
  },

  async updateOffice(id: number, data: OfficeUpdateRequest): Promise<OfficeResponse> {
    const response = await httpClient.put<OfficeResponse>(`/offices/${id}`, data);
    return response.data;
  },

  async deleteOffice(id: number): Promise<void> {
    await httpClient.delete(`/offices/${id}`);
  }
};
