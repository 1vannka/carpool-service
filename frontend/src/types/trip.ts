export interface TripCreateRequest {
  officeId: number | null;
  departureTime: Date | null;
  estimatedDuration: number | null;
  totalSeats: number;
  carModel: string;
  carColor: string;
  carPlate: string;
  routePath: number[][];
}
