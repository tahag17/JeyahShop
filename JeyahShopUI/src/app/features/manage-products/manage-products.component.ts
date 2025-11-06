// features/manage-products/manage-products.component.ts
import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { ProductService } from '../../core/services/product/product.service';
import { ProductResponse } from '../../shared/models/product-response';
import { PageResponse } from '../../shared/models/page-response';
import { Subscription } from 'rxjs';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-manage-products',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, RouterModule],
  templateUrl: './manage-products.component.html',
  styleUrls: ['./manage-products.component.scss'],
})
export class ManageProductsComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);

  products: ProductResponse[] = [];
  loading = true;
  error: string | null = null;

  currentPage = 0;
  totalPages = 0;
  pageSize = 10;

  private sub?: Subscription;

  ngOnInit(): void {
    this.fetchProducts();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  fetchProducts(page = 0) {
    this.loading = true;
    this.error = null;

    this.sub = this.productService
      .getAllProducts(page, this.pageSize)
      .subscribe({
        next: (data: PageResponse<ProductResponse>) => {
          this.products = data.content;
          this.totalPages = data.totalPages;
          this.currentPage = data.page;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Erreur lors du chargement des produits.';
          this.loading = false;
        },
      });
  }

  deleteProduct(id: number) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) return;

    this.productService.deleteProduct(id).subscribe({
      next: () => this.fetchProducts(this.currentPage),
      error: (err) => alert('Erreur lors de la suppression du produit'),
    });
  }

  goToPage(page: number) {
    this.fetchProducts(page);
  }
}
