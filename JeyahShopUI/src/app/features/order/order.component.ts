import { Component, OnInit } from '@angular/core';
import { Order } from '../../shared/models/order.model';
import { OrderService } from '../../core/services/order/order.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss',
})
export class OrderComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  error: string | null = null;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.error = null;
    this.orderService.getUserOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
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
        this.orders.push(order);
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
        if (index !== -1) this.orders[index] = updatedOrder;
      },
      error: (err) => {
        this.error =
          err.message || "Erreur lors de l'annulation de la commande";
      },
    });
  }
}
