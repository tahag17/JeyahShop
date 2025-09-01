import { Address } from './address.model';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone?: string | null;
  enabled: boolean;
  roles: string[];
  address: Address;
  creationDate: string; // or Date if you want to parse it
  lastModifiedDate?: string | null; // or Date
}
