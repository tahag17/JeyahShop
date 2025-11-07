// shared/models/order-status.enum.ts
export enum OrderStatus {
  PENDING = 'PENDING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED', // optional if backend uses this
}
