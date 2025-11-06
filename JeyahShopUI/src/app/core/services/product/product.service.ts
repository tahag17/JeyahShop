import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { PageResponse } from '../../../shared/models/page-response';
import { SimpleProductResponse } from '../../../shared/models/simple-product-response';
import { ProductResponse } from '../../../shared/models/product-response';
import { ProductRequest } from '../../../shared/models/product-request';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}public/api/products`;
  private managerApiUrl = `${environment.apiBaseUrl}manager/api/products`;

  constructor() {}

  // Get all products (paginated)
  getProducts(
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<SimpleProductResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SimpleProductResponse>>(this.apiUrl, {
      params,
    });
  }

  // Search products by keyword
  searchProducts(
    keyword: string,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<SimpleProductResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<SimpleProductResponse>>(
      `${this.apiUrl}/search/${keyword}`,
      { params }
    );
  }

  // Search products with filters
  searchProductsWithFilters(
    keyword: string = 'usb',
    minPrice?: number,
    maxPrice?: number,
    tags?: string[],
    sortBy: string = 'rate',
    sortDirection: string = 'desc',
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<SimpleProductResponse>> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection)
      .set('page', page)
      .set('size', size);

    if (minPrice != null) params = params.set('minPrice', minPrice);
    if (maxPrice != null) params = params.set('maxPrice', maxPrice);
    if (tags?.length) params = params.set('tags', tags.join(','));

    return this.http.get<PageResponse<SimpleProductResponse>>(
      `${this.apiUrl}/search`,
      { params }
    );
  }

  // -------------------------------
  // Manager methods
  // -------------------------------
  getAllProducts(
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<ProductResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<ProductResponse>>(this.managerApiUrl, {
      params,
      withCredentials: true,
    });
  }

  getProductById(id: number): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.managerApiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  addProduct(
    product: ProductRequest,
    images?: File[]
  ): Observable<ProductResponse> {
    const formData = new FormData();
    formData.append(
      'product',
      new Blob([JSON.stringify(product)], { type: 'application/json' })
    );

    if (images && images.length > 0) {
      for (let i = 0; i < images.length; i++) {
        formData.append('images', images[i]);
      }
    }

    return this.http.post<ProductResponse>(this.managerApiUrl, formData, {
      withCredentials: true,
    });
  }

  updateProduct(
    id: number,
    product: ProductRequest
  ): Observable<ProductResponse> {
    return this.http.patch<ProductResponse>(
      `${this.managerApiUrl}/${id}`,
      product,
      { withCredentials: true }
    );
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.managerApiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  addImagesToProduct(id: number, images: File[]): Observable<any> {
    const formData = new FormData();
    for (let i = 0; i < images.length; i++) {
      formData.append('images', images[i]);
    }
    return this.http.post(`${this.managerApiUrl}/${id}/images`, formData, {
      withCredentials: true,
    });
  }
}
