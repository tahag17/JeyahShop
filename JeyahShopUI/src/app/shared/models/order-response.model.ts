import { OrderDetailsResponse } from './order-details-response.model';

export interface OrderResponse {
  orderId: number;
  createdAt: string; // backend sends ISO string
  status: 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  products: OrderDetailsResponse[];
  totalPrice: number;
}
