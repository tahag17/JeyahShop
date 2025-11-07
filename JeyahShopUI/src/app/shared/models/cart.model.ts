import { CartItem } from './cart-item.model';

export interface Cart {
  id: number;
  totalPrice: number;
  items: CartItem[];
}
