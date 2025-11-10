// src/app/shared/models/product-response.ts

export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  description: string;
  categoryId: number;
  stockQuantity: number;
  imageUrls: string[];
  rate: number;
  available: boolean;
}
