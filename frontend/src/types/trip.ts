export interface TripCreateRequest {
  officeId: number;
  departureTime: string; // Формат Date/ISO
  estimatedDuration: number;
  totalSeats: number;
  carModel: string;
  carColor: string;
  carPlate: string;
  routePath: number[][]; // Массив координат
}

export interface TripResponse {
  id: number;
  officeId: number;
  departureTime: string;
  estimatedDuration: number;
  totalSeats: number;
  availableSeats: number;
  carModel: string;
  carColor: string;
  carPlate: string;
  status: string;
  routePath: number[][];
}
