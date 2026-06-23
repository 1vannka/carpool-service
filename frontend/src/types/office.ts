export interface OfficeResponse {
  id: number;
  name: string;
  city: string;
  address: string;
  location: number[];
}

export interface OfficeCreateRequest {
  name: string;
  city: string;
  address: string;
  location: number[];
}

export type OfficeUpdateRequest = OfficeCreateRequest;
