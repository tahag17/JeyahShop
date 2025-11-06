// src/app/shared/models/product-response.ts

export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  description: string;
  category: string;
  stockQuantity: number;
  imageUrls: string[];
  rate: number;
  available: boolean;
}
