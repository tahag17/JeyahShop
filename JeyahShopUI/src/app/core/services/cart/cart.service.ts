import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart } from '../../../shared/models/cart.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/cart`;

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl, { withCredentials: true });
  }

  addToCart(productId: number, quantity: number = 1): Observable<Cart> {
    return this.http.post<Cart>(
      `${this.apiUrl}/add/${productId}?quantity=${quantity}`,
      null,
      { withCredentials: true }
    );
  }

  updateCartItem(cartItemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(
      `${this.apiUrl}/update/${cartItemId}?quantity=${quantity}`,
      {},
      { withCredentials: true }
    );
  }

  removeCartItem(cartItemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/remove/${cartItemId}`, {
      withCredentials: true,
    });
  }

  clearCart(): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/clear`, {
      withCredentials: true,
    });
  }
}
