export interface RideRequestCreateRequest {
  officeId: number | null;
  pickupLocation: number[];
  targetTime: Date | null;
  toleranceTime: number;
}
