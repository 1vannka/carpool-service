export interface UserProfileResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  telegramAlias: string | null;
  vkAlias: string | null;
  role: string;
}

export interface UpdateProfileRequest {
  telegramAlias: string | null;
  vkAlias: string | null;
}
