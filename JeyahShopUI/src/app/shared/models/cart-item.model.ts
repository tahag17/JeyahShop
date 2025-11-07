export interface CartItem {
  id: number;
  quantity: number;
  productId: number;
  productName: string;
  price: number;
  imageUrl?: string;
}
