import { Address } from './address.model';

export interface BackendUser {
  id: number;
  firstName: string | null; // backend can send null
  lastName: string | null; // backend can send null
  fullName: string | null; // backend can send null
  email: string | null; // backend might send null if user registered with Google?
  phone?: string | null; // optional and nullable
  enabled: boolean;
  hasPassword: boolean;
  roles: string[];
  address: Address | null; // backend can send null if no address yet
  creationDate: number[] | null; // array from LocalDateTime
  lastModifiedDate?: number[] | null;
}
