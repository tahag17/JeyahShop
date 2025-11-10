// src/app/shared/models/product-request.ts

export interface ProductRequest {
  id?: number; // optional, 0 or undefined for new products
  name: string;
  price: number;
  description: string;
  categoryId: number;
  stockQuantity: number;
  imageUrls?: string[]; // optional, can be empty
}
