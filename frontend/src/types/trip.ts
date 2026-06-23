export interface TripCreateRequest {
  officeId: number;
  departureTime: string;
  estimatedDuration: number;
  totalSeats: number;
  carModel: string;
  carColor: string;
  carPlate: string;
  routePath: number[][];
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

export interface TripDetailedResponse {
  id: number;
  officeId: number;
  driverFirstName: string;
  driverLastName: string;
  telegramAlias: string;
  vkAlias: string;
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

export enum BookingStatus {
  WAITING_APPROVAL = 'WAITING_APPROVAL',
  CONFIRMED = 'CONFIRMED',
  REJECTED = 'REJECTED'
}

export interface TripPassengerResponse {
  tripId: number;
  passengerId: number;
  status: BookingStatus | string;
}

export interface TripPassengerDetailedResponse {
  tripId: number;
  firstName: string;
  lastName: string;
  telegramAlias: string;
  vkAlias: string;
  passengerId: number;
  status: BookingStatus | string;
  pickupLocation: number[];
}
