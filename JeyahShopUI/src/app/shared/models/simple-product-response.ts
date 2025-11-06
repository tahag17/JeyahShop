export interface SimpleProductResponse {
  id: number;
  name: string;
  price: number;
  rate: number;
  available: boolean;
  imageUrl?: string; // optional, matches Optional<String> from backend
}
