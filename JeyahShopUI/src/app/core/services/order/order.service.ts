import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Order } from '../../../shared/models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/orders`;

  // Get all orders for current user
  getUserOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl, { withCredentials: true });
  }

  // Make order from current cart
  makeOrder(): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/make`, null, {
      withCredentials: true,
    });
  }

  // Cancel an order
  cancelOrder(orderId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/cancel/${orderId}`, null, {
      withCredentials: true,
    });
  }

  // Optional: get single order details
  getOrderById(orderId: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`, {
      withCredentials: true,
    });
  }
}
