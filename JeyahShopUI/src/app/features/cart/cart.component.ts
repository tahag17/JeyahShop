import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CartService } from '../../core/services/cart/cart.service';
import { OrderService } from '../../core/services/order/order.service';
import { Cart } from '../../shared/models/cart.model';
import { OrderResponse } from '../../shared/models/order-response.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, FormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
})
export class CartComponent implements OnInit {
  private cartService = inject(CartService);
  private orderService = inject(OrderService);

  cart: Cart | null = null;
  loading = false;
  error: string | null = null;
  orderSuccess: OrderResponse | null = null;

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart() {
    this.loading = true;
    this.error = null;
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load cart:', err);
        this.error = 'Erreur lors du chargement du panier.';
        this.loading = false;
      },
    });
  }

  updateQuantity(itemId: number, quantity: number) {
    if (quantity < 1) return;
    this.loading = true;
    this.cartService.updateCartItem(itemId, quantity).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to update cart item:', err);
        this.error = 'Erreur lors de la mise à jour du panier.';
        this.loading = false;
      },
    });
  }

  removeItem(itemId: number) {
    this.loading = true;
    this.cartService.removeCartItem(itemId).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to remove cart item:', err);
        this.error = 'Erreur lors de la suppression du produit.';
        this.loading = false;
      },
    });
  }

  clearCart() {
    this.loading = true;
    this.cartService.clearCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to clear cart:', err);
        this.error = 'Erreur lors du vidage du panier.';
        this.loading = false;
      },
    });
  }

  onQuantityChange(event: Event, itemId: number) {
    const input = event.target as HTMLInputElement;
    const value = Number(input.value) || 1;
    this.updateQuantity(itemId, value);
  }

  // ✅ Place order
  makeOrder() {
    if (!this.cart || this.cart.items.length === 0) return;

    this.loading = true;
    this.orderService.makeOrder().subscribe({
      next: (order) => {
        this.orderSuccess = order;
        this.clearCart(); // optionally clear cart after order
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to place order:', err);
        this.error = 'Erreur lors de la création de la commande.';
        this.loading = false;
      },
    });
  }
}
