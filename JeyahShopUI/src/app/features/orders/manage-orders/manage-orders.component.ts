import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ManagerOrderResponse } from '../../../shared/models/manager-order-response.model';
import { OrderService } from '../../../core/services/order/order.service';
import { PageResponse } from '../../../shared/models/page-response';

@Component({
  selector: 'app-manage-orders',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf],
  templateUrl: './manage-orders.component.html',
  styleUrls: ['./manage-orders.component.scss'],
})
export class ManageOrdersComponent implements OnInit, OnDestroy {
  private orderService = inject(OrderService);

  orders: ManagerOrderResponse[] = [];
  loading = true;
  error: string | null = null;

  currentPage = 0;
  totalPages = 0;
  pageSize = 10;

  private sub?: Subscription;

  ngOnInit(): void {
    this.fetchOrders();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  fetchOrders(page = 0) {
    this.loading = true;
    this.error = null;

    this.sub = this.orderService.getAllOrders(page, this.pageSize).subscribe({
      next: (data: PageResponse<ManagerOrderResponse>) => {
        // ✅ use ManagerOrderResponse
        this.orders = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des commandes.';
        this.loading = false;
      },
    });
  }

  cancelOrder(id: number) {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette commande ?')) return;

    this.orderService.cancelOrderManager(id).subscribe({
      next: () => this.fetchOrders(this.currentPage),
      error: () => alert('Erreur lors de l’annulation de la commande'),
    });
  }

  goToPage(page: number) {
    this.fetchOrders(page);
  }
}
