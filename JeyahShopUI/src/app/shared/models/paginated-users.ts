import { User } from './user.model';

export interface PaginatedUsers {
  totalItems: number;
  totalPages: number;
  pageSize: number;
  currentPage: number;
  users: User[];
}
