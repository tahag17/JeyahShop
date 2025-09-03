import { Address } from './address.model';

export interface BackendUser {
  id: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone?: string | null;
  enabled: boolean;
  roles: string[];
  address: Address;
  creationDate: number[] | null;
  lastModifiedDate?: number[] | null;
}
