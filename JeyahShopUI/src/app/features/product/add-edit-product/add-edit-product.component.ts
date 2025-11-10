// features/manage-products/add-edit-product.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { ProductService } from '../../../core/services/product/product.service';
import { ProductRequest } from '../../../shared/models/product-request';
import { ProductResponse } from '../../../shared/models/product-response';
import { CategoryService } from '../../../core/services/category/category.service';
import { Category } from '../../../shared/models/category.model';

@Component({
  selector: 'app-add-edit-product',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf, RouterModule],
  templateUrl: './add-edit-product.component.html',
  styleUrls: ['./add-edit-product.component.scss'],
})
export class AddEditProductComponent implements OnInit {
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);

  private router = inject(Router);
  private route = inject(ActivatedRoute);

  product: ProductRequest = {
    id: 0,
    name: '',
    price: 0,
    description: '',
    categoryId: 0, // 🟢 changed to categoryId
    stockQuantity: 0,
    imageUrls: [],
  };

  categories: Category[] = [];
  images: File[] = [];
  isEdit = false;

  ngOnInit() {
    // Fetch categories
    this.categoryService.getAllCategories().subscribe({
      next: (res) => {
        console.log('Categories loaded:', res); // ✅ add this line for debugging
        this.categories = res;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        alert('Erreur lors du chargement des catégories');
      },
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.isEdit = true;
      this.productService.getProductById(id).subscribe({
        next: (data: ProductResponse) => {
          // Map ProductResponse → ProductRequest
          this.product = {
            id: data.id,
            name: data.name,
            price: data.price,
            description: data.description,
            categoryId: data.categoryId,
            stockQuantity: data.stockQuantity,
            imageUrls: data.imageUrls || [],
          };
        },
        error: () => alert('Erreur lors du chargement du produit'),
      });
    }
  }

  onFileChange(event: any) {
    this.images = Array.from(event.target.files);
  }

  submit() {
    if (this.isEdit) {
      if (!this.product.id) return;

      this.productService
        .updateProduct(this.product.id, this.product)
        .subscribe({
          next: () => this.router.navigate(['/dashboard/products']),
          error: () => alert('Erreur lors de la mise à jour du produit'),
        });
    } else {
      this.productService.addProduct(this.product, this.images).subscribe({
        next: () => this.router.navigate(['/dashboard/products']),
        error: () => alert('Erreur lors de l’ajout du produit'),
      });
    }
  }
}
