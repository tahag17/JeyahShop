import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { AuthService } from '../../../core/services/auth/auth.service';
import {
  RatingService,
  UserRating,
} from '../../../core/services/rating/rating.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-rating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="flex items-center gap-2">
      <ng-container *ngFor="let star of stars; index as i">
        <i
          class="text-yellow-400 text-xl cursor-pointer"
          [ngClass]="getStarClass(i)"
          (click)="selectRating(i + 1)"
          [title]="
            userHasRated
              ? 'Cliquez pour modifier votre note'
              : 'Cliquez pour noter'
          "
        ></i>
      </ng-container>

      <span *ngIf="userHasRated" class="text-gray-500 ml-2 text-sm">
        Vous avez noté ce produit: {{ currentRate | number : '1.1-2' }} ⭐ (vous
        pouvez changer)
      </span>
      <span *ngIf="!userHasRated" class="text-gray-500 ml-2 text-sm">
        Laissez votre note ! Moyenne: {{ currentRate | number : '1.1-2' }} ⭐
      </span>
    </div>
  `,
})
export class ProductRatingComponent implements OnInit {
  @Input() productId!: number;
  @Input() initialRate = 0; // fallback average rate
  @Output() ratingUpdated = new EventEmitter<void>();

  stars = Array(5);
  selectedRating = 0;
  showModal = false;
  userHasRated = false;
  currentRate = 0;

  private ratingService = inject(RatingService);
  private authService = inject(AuthService);

  ngOnInit() {
    console.log(
      `[ProductRating] Init for productId=${this.productId}, initialRate=${this.initialRate}`
    );

    // Start with average rate
    this.currentRate = this.initialRate;

    // Fetch ratings from backend
    this.ratingService.getUserRating(this.productId).subscribe({
      next: (res: UserRating[]) => {
        console.log(`[ProductRating] response:`, res);

        const userRating = res.find(
          (r) => r.userId === this.authService.currentUser?.id
        );

        if (userRating) {
          this.currentRate = userRating.rating;
          this.userHasRated = true;
        } else if (res[0]?.productAverageRating != null) {
          this.currentRate = res[0].productAverageRating;
        }
      },
      error: (err) => {
        console.error(err);
        this.userHasRated = false;
      },
    });
  }

  getStarClass(index: number) {
    const diff = this.currentRate - index;
    if (diff >= 0.7) return 'fa-solid fa-star';
    if (diff >= 0.3) return 'fa-solid fa-star-half';
    return 'fa-regular fa-star';
  }

  selectRating(value: number) {
    if (!this.authService.isLoggedIn()) {
      alert('Vous devez être connecté pour noter ce produit.');
      return;
    }
    this.selectedRating = value;
    this.confirmRating(); // directly confirm rating
  }

  confirmRating() {
    this.ratingService
      .rateProduct(this.productId, this.selectedRating)
      .subscribe({
        next: () => {
          this.currentRate = this.selectedRating;
          this.userHasRated = true;
          this.ratingUpdated.emit();
        },
        error: (err) => {
          alert(err.error?.message || 'Impossible de noter le produit.');
        },
      });
  }
}
