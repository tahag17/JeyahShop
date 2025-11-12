import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { ProductService } from '../../../core/services/product/product.service';
import { SimpleProductResponse } from '../../../shared/models/simple-product-response';
import { PageResponse } from '../../../shared/models/page-response';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { SearchService } from '../../../core/services/search/search.service';
import { Subscription } from 'rxjs';
import { CartService } from '../../../core/services/cart/cart.service';
import { ProductRatingComponent } from '../../rating/product-rating/product-rating.component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [NgIf, NgFor, CommonModule, ProductRatingComponent, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
})
export class ProductListComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);
  private searchService = inject(SearchService);
  private cartService = inject(CartService);
  private router = inject(Router);

  products: SimpleProductResponse[] = [];
  loading = true;
  error: string | null = null;
  message: string | null = null;
  showFilters = false;

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  private searchSub?: Subscription;

  filterKeyword: string = '';
  minPrice: number = 0;
  maxPrice: number = 1000;
  sortBy: string = 'rate';
  sortDirection: string = 'desc';

  ngOnInit(): void {
    this.searchSub = this.searchService.keyword$.subscribe((keyword) => {
      const trimmed = keyword.trim();
      if (trimmed) {
        this.filterKeyword = trimmed; // ✅ store keyword for filtering
        this.searchProducts(trimmed);
        this.showFilters = true;
      } else {
        this.products = [];
        this.showFilters = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    console.warn('[Image Error] Original src:', img.src);
    img.style.display = 'none'; // hide the broken image
    // Optionally, you can replace it with a placeholder icon dynamically if needed
  }

  // Ensure min <= max
  checkRange() {
    if (this.minPrice > this.maxPrice) {
      const tmp = this.minPrice;
      this.minPrice = this.maxPrice;
      this.maxPrice = tmp;
    }
  }

  applyPriceFilter() {
    this.applyFilters(0);
  }

  applyFilters(page = 0) {
    this.loading = true;
    this.error = null;
    this.checkRange();

    const keywordToUse = this.filterKeyword.trim();

    this.productService
      .searchProductsWithFilters(
        keywordToUse, // ✅ use the stored keyword
        this.minPrice,
        this.maxPrice,
        this.sortBy,
        this.sortDirection,
        page,
        this.pageSize
      )
      .subscribe({
        next: (data) => {
          this.products = data.content;
          this.totalPages = data.totalPages;
          this.currentPage = data.page;
          this.loading = false;
          this.showMobileFilters = false;
        },
        error: (err) => {
          console.error('Failed to filter products:', err);
          this.error = 'Erreur lors de la recherche filtrée.';
          this.loading = false;
        },
      });
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

  getStarClass(rate: number | string | undefined, starIndex: number): string {
    const r = Number(rate ?? 0);
    if (r >= starIndex) return 'fa-solid fa-star';
    if (r >= starIndex - 0.5) return 'fa-solid fa-star-half-stroke';
    return 'fa-regular fa-star';
  }

  goToPage(page: number) {
    if (this.showFilters) this.applyFilters(page);
    else this.fetchProducts(page);
  }

  goToDetails(productId: number) {
    this.router.navigate(['/products', productId]);
  }

  addToCart(productId: number) {
    this.cartService.addToCart(productId, 1).subscribe({
      next: () => {
        this.message = 'Produit ajouté au panier !';
        // Trigger header cart modal open
        const header = document.querySelector('app-header') as any;
        if (header) header.cartDropdownOpen = true;

        setTimeout(() => {
          this.message = null;
          if (header) header.cartDropdownOpen = false;
        }, 2000); // show for 2 seconds
      },
      error: () => {
        this.error = 'Impossible d’ajouter le produit au panier.';
        setTimeout(() => (this.error = null), 3000);
      },
    });
  }

  showMobileFilters = false;
  toggleMobileFilters() {
    this.showMobileFilters = !this.showMobileFilters;
  }

  setSortDirection(direction: string) {
    if (this.sortDirection !== direction) {
      this.sortDirection = direction;
      this.applyFilters(); // automatically reapply filters
    }
  }
}
