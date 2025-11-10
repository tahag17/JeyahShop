import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ManagerOrderResponse } from '../../../shared/models/manager-order-response.model';
import { OrderService } from '../../../core/services/order/order.service';
import { PageResponse } from '../../../shared/models/page-response';
import { FormsModule } from '@angular/forms';
import { OrderStatus } from '../../../shared/models/order-status.enum';

interface ManagerOrderResponseWithDropdown extends ManagerOrderResponse {
  isDropdownOpen: boolean;
}

@Component({
  selector: 'app-manage-orders',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, FormsModule],
  templateUrl: './manage-orders.component.html',
  styleUrls: ['./manage-orders.component.scss'],
})
export class ManageOrdersComponent implements OnInit, OnDestroy {
  private orderService = inject(OrderService);

  orders: ManagerOrderResponseWithDropdown[] = [];
  loading = true;
  error: string | null = null;

  currentPage = 0;
  totalPages = 0;
  pageSize = 10;
  orderStatuses = Object.values(OrderStatus);
  isConfirmModalOpen = false;

  private sub?: Subscription;

  // To track which order is currently awaiting confirmation
  confirmChange: { id: number; newStatus: string } | null = null;

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
        // Add isDropdownOpen property for each order
        this.orders = data.content.map((order) => ({
          ...order,
          isDropdownOpen: false,
        }));
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
        this.loading = false;
      },
      error: () => {
        this.error = 'Erreur lors du chargement des commandes.';
        this.loading = false;
      },
    });
  }

  requestStatusChange(orderId: number, newStatus: string) {
    this.confirmChange = { id: orderId, newStatus };
    this.isConfirmModalOpen = true;
  }

  confirmStatusChange() {
    if (!this.confirmChange) return;
    const { id, newStatus } = this.confirmChange;

    this.orderService
      .updateOrderStatus(id, newStatus as OrderStatus)
      .subscribe({
        next: () => {
          this.closeConfirmModal();
          this.fetchOrders(this.currentPage);
        },
        error: () =>
          alert('Erreur lors du changement du statut de la commande'),
      });
  }

  closeConfirmModal() {
    this.isConfirmModalOpen = false;
    this.confirmChange = null;
  }

  goToPage(page: number) {
    this.fetchOrders(page);
  }
}
