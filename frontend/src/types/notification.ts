export interface SseNotification {
  type: string;
  tripId: number | null;
  passengerId: number;
  message: string;
}
