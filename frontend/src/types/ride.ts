export interface RideRequestCreateRequest {
  officeId: number | null;
  pickupLocation: number[];
  targetTime: Date | null;
  toleranceTime: number;
}

export interface RideRequestResponse {
  id: number;
  passengerId: number;
  officeId: number;
  pickupLocation: number[];
  targetTime: string;
  toleranceTime: number;
  status: string;
}
