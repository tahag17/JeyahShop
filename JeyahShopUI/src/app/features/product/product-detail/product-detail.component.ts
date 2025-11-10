import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../core/services/product/product.service';
import { ProductResponse } from '../../../shared/models/product-response';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../core/services/cart/cart.service';
import { CategoryService } from '../../../core/services/category/category.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private categoryService = inject(CategoryService);

  categoryName: string | null = null;
  product?: ProductResponse;
  loading = true;
  error: string | null = null;
  message: string | null = null;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.error = 'Produit introuvable';
      this.loading = false;
      return;
    }

    const productId = Number(idParam);
    if (isNaN(productId)) {
      this.error = 'ID de produit invalide';
      this.loading = false;
      return;
    }

    this.productService.getProductById(productId).subscribe({
      next: (data) => {
        this.product = data;

        // 🟢 Fetch category name using categoryId
        if (data.categoryId) {
          this.categoryService.getCategoryById(data.categoryId).subscribe({
            next: (cat) => (this.categoryName = cat.name),
            error: () => (this.categoryName = 'Inconnu'),
          });
        }

        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement du produit';
        this.loading = false;
      },
    });
  }

  addToCart(productId: number) {
    this.cartService.addToCart(productId, 1).subscribe({
      next: () => {
        this.message = 'Produit ajouté au panier ! 🛒';
        setTimeout(() => (this.message = null), 3000);
      },
      error: () => {
        this.error = 'Impossible d’ajouter le produit au panier.';
        setTimeout(() => (this.error = null), 3000);
      },
    });
  }

  /** returns a string with the right FontAwesome classes for a given star index */
  getStarClass(rate: number | string | undefined, starIndex: number): string {
    const r = Number(rate ?? 0);
    if (r >= starIndex) return 'fa-solid fa-star';
    if (r >= starIndex - 0.5) return 'fa-solid fa-star-half-stroke';
    return 'fa-regular fa-star';
  }

  goBack() {
    this.router.navigate(['/products']); // or wherever your list route is
  }
}
