import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';
import { Order } from '../../../shared/models/order.model';
import { OrderResponse } from '../../../shared/models/order-response.model';
import { OrderStatus } from '../../../shared/models/order-status.enum';
import { PageResponse } from '../../../shared/models/page-response';
import { ManagerOrderResponse } from '../../../shared/models/manager-order-response.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/orders`;
  private managerApiUrl = `${environment.apiBaseUrl}manager/api/orders`;

  // ------------------------
  // User methods
  // ------------------------

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

  // ------------------------
  // Manager methods
  // ------------------------
  getAllOrders(
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<ManagerOrderResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<PageResponse<ManagerOrderResponse>>(this.managerApiUrl, {
        params,
        withCredentials: true,
      })
      .pipe(
        tap((response) => {
          console.log('Manager orders response:', response);
        })
      );
  }
  getOrderByIdManager(id: number): Observable<ManagerOrderResponse> {
    return this.http.get<ManagerOrderResponse>(`${this.managerApiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  updateOrderStatus(
    id: number,
    status: OrderStatus
  ): Observable<ManagerOrderResponse> {
    return this.http.patch<ManagerOrderResponse>(
      `${this.managerApiUrl}/${id}/status`,
      null,
      {
        params: { status },
        withCredentials: true,
      }
    );
  }

  cancelOrderManager(id: number): Observable<ManagerOrderResponse> {
    return this.http.post<ManagerOrderResponse>(
      `${this.managerApiUrl}/${id}/cancel`,
      null,
      {
        withCredentials: true,
      }
    );
  }
}
