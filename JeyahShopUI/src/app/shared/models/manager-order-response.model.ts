// shared/models/manager-order-response.model.ts
import { OrderDetailsResponse } from './order-details-response.model';

export interface ManagerOrderResponse {
  id: number; // use for table row
  userEmail: string; // show which user
  createdAt: string;
  status: 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'COMPLETED';
  products: OrderDetailsResponse[];
  totalPrice: number;
}
