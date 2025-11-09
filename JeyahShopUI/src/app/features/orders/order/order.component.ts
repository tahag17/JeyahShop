import { Component, inject, OnInit } from '@angular/core';
import { Order } from '../../../shared/models/order.model';
import { OrderService } from '../../../core/services/order/order.service';
import { CommonModule } from '@angular/common';
import { ProductRatingComponent } from '../../rating/product-rating/product-rating.component';
import { convertToDate } from '../../../utils/date.utils';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule, ProductRatingComponent],
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss'], // fixed typo
})
export class OrderComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  error: string | null = null;

  private orderService = inject(OrderService);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.error = null;
    this.orderService.getUserOrders().subscribe({
      next: (orders) => {
        // Ensure createdAt is always a string for the interface
        this.orders = orders.map((o) => ({
          ...o,
          createdAt: convertToDate(o.createdAt)?.toISOString() || '',
        }));
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message || 'Erreur lors du chargement des commandes';
        this.loading = false;
      },
    });
  }

  makeOrder() {
    this.orderService.makeOrder().subscribe({
      next: (order) => {
        const newOrder: Order = {
          ...order,
          createdAt:
            convertToDate(order.createdAt)?.toISOString() ||
            new Date().toISOString(),
        };
        this.orders.push(newOrder);
      },
      error: (err) => {
        this.error = err.message || 'Erreur lors de la création de la commande';
      },
    });
  }

  cancelOrder(orderId: number) {
    this.orderService.cancelOrder(orderId).subscribe({
      next: (updatedOrder) => {
        const index = this.orders.findIndex((o) => o.orderId === orderId);
        if (index !== -1) {
          this.orders[index] = {
            ...updatedOrder,
            createdAt:
              convertToDate(updatedOrder.createdAt)?.toISOString() ||
              new Date().toISOString(),
          };
        }
      },
      error: (err) => {
        this.error =
          err.message || "Erreur lors de l'annulation de la commande";
      },
    });
  }

  onRatingUpdated() {
    // Optional: refresh the order list or product ratings if needed
    this.loadOrders();
  }
}
