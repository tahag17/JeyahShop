import { Component, inject, OnInit } from '@angular/core';
import { CategoryService } from '../../../core/services/category/category.service';
import { Category } from '../../../shared/models/category.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-categories-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories-list.component.html',
  styleUrl: './categories-list.component.scss',
})
export class CategoriesListComponent implements OnInit {
  private categoryService = inject(CategoryService);

  categories: Category[] = [];
  newCategoryName: string = '';
  loading = true;
  error: string | null = null;
  message: string | null = null;

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories() {
    this.loading = true;
    this.categoryService.getAllCategories().subscribe({
      // ✅ add () here
      next: (res) => {
        this.categories = res;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erreur lors du chargement des catégories';
        this.loading = false;
      },
    });
  }

  addCategory() {
    if (!this.newCategoryName.trim()) return;

    const category: Category = { id: 0, name: this.newCategoryName.trim() };
    this.categoryService.addCategory(category).subscribe({
      next: (res) => {
        this.message = `Catégorie "${res.name}" ajoutée !`;
        this.newCategoryName = '';
        this.loadCategories();
        setTimeout(() => (this.message = null), 3000);
      },
      error: () => {
        this.error = 'Erreur lors de l’ajout de la catégorie';
        setTimeout(() => (this.error = null), 3000);
      },
    });
  }

  deleteCategory(id: number) {
    if (!confirm('Voulez-vous vraiment supprimer cette catégorie ?')) return;

    this.categoryService.deleteCategory(id).subscribe({
      next: () => {
        this.message = 'Catégorie supprimée !';
        this.loadCategories();
        setTimeout(() => (this.message = null), 3000);
      },
      error: () => {
        this.error = 'Erreur lors de la suppression';
        setTimeout(() => (this.error = null), 3000);
      },
    });
  }
}
