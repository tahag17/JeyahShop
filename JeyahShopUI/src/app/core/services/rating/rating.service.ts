import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RatingService {
  constructor() {}

  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/ratings`;

  // Submit or update rating
  rateProduct(productId: number, rate: number) {
    console.log('Calling rateProduct:', { productId, rate });

    return this.http
      .post(`${this.apiUrl}`, { productId, rate }, { withCredentials: true })
      .pipe(
        tap({
          next: (res) => console.log('Rating submitted successfully:', res),
          error: (err) => console.error('Error submitting rating:', err),
        })
      );
  }

  // Optional: fetch rating of current user for a product
  getUserRating(productId: number) {
    return this.http.get<{ rate: number }>(
      `${this.apiUrl}/product/${productId}`,
      { withCredentials: true }
    );
  }

  // Optional: fetch all ratings for a product
  getProductRatings(productId: number) {
    return this.http.get<{ user: string; rate: number; comment?: string }[]>(
      `${this.apiUrl}/product/${productId}/all`
    );
  }
}
