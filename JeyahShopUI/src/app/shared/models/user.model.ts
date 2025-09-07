import { Address } from './address.model';

export interface User {
  id: number;
  firstName: string | null;
  lastName: string | null;
  fullName: string | null;
  email: string | null;
  phone?: string | null;
  enabled: boolean;
  hasPassword: boolean;
  roles: string[];
  address: Address | null;
  creationDate: Date | null;
  lastModifiedDate?: Date | null;
}
