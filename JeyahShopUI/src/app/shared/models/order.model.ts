import { OrderDetails } from './order-details.model';

export interface Order {
  orderId: number;
  createdAt: string; // ISO date string from backend
  status: 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  products: OrderDetails[];
  totalPrice: number;
}
