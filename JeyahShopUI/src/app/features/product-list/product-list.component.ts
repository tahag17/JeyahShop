import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { ProductService } from '../../core/services/product/product.service';
import { SimpleProductResponse } from '../../shared/models/simple-product-response';
import { PageResponse } from '../../shared/models/page-response';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { SearchService } from '../../core/services/search/search.service';
import { Subscription } from 'rxjs';
import { CartService } from '../../core/services/cart/cart.service';
import { ProductRatingComponent } from '../rating/product-rating/product-rating.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [NgIf, NgFor, CommonModule, ProductRatingComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
})
export class ProductListComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);
  private searchService = inject(SearchService);
  private cartService = inject(CartService); // ✅ inject CartService

  products: SimpleProductResponse[] = [];
  loading = true;
  error: string | null = null;
  message: string | null = null; // ✅ feedback message

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  private searchSub?: Subscription;

  ngOnInit(): void {
    this.fetchProducts();

    this.searchSub = this.searchService.keyword$.subscribe((keyword) => {
      if (keyword.trim()) {
        this.searchProducts(keyword);
      } else {
        this.fetchProducts(0);
      }
    });
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  fetchProducts(page = 0) {
    this.loading = true;
    this.error = null;

    this.productService.getProducts(page, this.pageSize).subscribe({
      next: (data: PageResponse<SimpleProductResponse>) => {
        this.products = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load products:', err);
        this.error = 'Erreur lors du chargement des produits.';
        this.loading = false;
      },
    });
  }

  searchProducts(keyword: string, page = 0) {
    this.loading = true;
    this.error = null;

    this.productService.searchProducts(keyword, page, this.pageSize).subscribe({
      next: (data: PageResponse<SimpleProductResponse>) => {
        this.products = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.page;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to search products:', err);
        this.error = 'Erreur lors de la recherche.';
        this.loading = false;
      },
    });
  }

  /** returns a string with the right FontAwesome classes for a given star index */
  getStarClass(rate: number | string | undefined, starIndex: number): string {
    const r = Number(rate ?? 0); // force numeric
    if (r >= starIndex) return 'fa-solid fa-star';
    if (r >= starIndex - 0.5) return 'fa-solid fa-star-half-stroke';
    return 'fa-regular fa-star';
  }

  goToPage(page: number) {
    const keyword = this.searchService.getCurrentKeyword();
    if (keyword?.trim()) {
      this.searchProducts(keyword, page);
    } else {
      this.fetchProducts(page);
    }
  }

  addToCart(productId: number) {
    this.cartService.addToCart(productId, 1).subscribe({
      next: (cart) => {
        this.message = 'Produit ajouté au panier ! 🛒';
        setTimeout(() => (this.message = null), 3000); // auto-hide after 3s
      },
      error: () => {
        this.error = 'Impossible d’ajouter le produit au panier.';
        setTimeout(() => (this.error = null), 3000);
      },
    });
  }
}
