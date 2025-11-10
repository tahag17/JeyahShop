import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';
import { Category } from '../../../shared/models/category.model';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}public/api/categories`;
  private managerApiUrl = `${environment.apiBaseUrl}manager/api/categories`;

  categories: Category[] = [];

  constructor() {}

  // -------------------------------
  // Public methods
  // -------------------------------
  getAllCategories(): Observable<Category[]> {
    return this.http
      .get<Category[]>(this.apiUrl, { withCredentials: true })
      .pipe(tap((res) => (this.categories = res)));
  }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  // -------------------------------
  // Manager methods
  // -------------------------------

  addCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.managerApiUrl, category, {
      withCredentials: true,
    });
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.managerApiUrl}/${id}`, {
      withCredentials: true,
    });
  }
}
