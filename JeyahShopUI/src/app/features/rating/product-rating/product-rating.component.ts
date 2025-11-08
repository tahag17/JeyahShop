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
import { RatingService } from '../../../core/services/rating/rating.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-rating',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="relative">
      <div class="flex items-center gap-1">
        <ng-container *ngFor="let star of stars; index as i">
          <i
            class="cursor-pointer text-yellow-400 text-xl"
            [ngClass]="getStarClass(i)"
            (click)="selectRating(i + 1)"
          ></i>
        </ng-container>
        <span *ngIf="!userHasRated" class="text-gray-500 ml-2 text-sm">
          Laissez votre note !
        </span>
      </div>

      <!-- Confirmation modal -->
      <div
        *ngIf="showModal"
        class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50"
      >
        <div class="bg-white p-6 rounded-2xl text-center">
          <p class="mb-4">Vous avez choisi {{ selectedRating }} étoile(s)</p>
          <button
            class="bg-green-500 text-white px-4 py-2 rounded-xl mr-2 hover:bg-green-600"
            (click)="confirmRating()"
          >
            Confirmer
          </button>
          <button
            class="bg-gray-300 px-4 py-2 rounded-xl hover:bg-gray-400"
            (click)="cancelRating()"
          >
            Annuler
          </button>
        </div>
      </div>
    </div>
  `,
})
export class ProductRatingComponent implements OnInit {
  @Input() productId!: number;
  @Input() initialRate = 0; // average rating of product
  @Output() ratingUpdated = new EventEmitter<void>();

  stars = Array(5);
  selectedRating = 0;
  showModal = false;
  userHasRated = false;
  currentRate = 0;

  private ratingService = inject(RatingService);
  private authService = inject(AuthService);

  ngOnInit() {
    this.currentRate = this.initialRate;
    this.userHasRated = this.currentRate > 0;
  }

  // Determine which icon to show
  getStarClass(index: number) {
    const diff = this.currentRate - index;
    if (diff >= 0.7) return 'fa-solid fa-star';
    if (diff >= 0.3) return 'fa-solid fa-star-half';
    return 'fa-regular fa-star';
  }

  // When user clicks on a star
  selectRating(value: number) {
    if (!this.authService.isLoggedIn()) {
      alert('Vous devez être connecté pour noter ce produit.');
      return;
    }
    this.selectedRating = value;
    this.showModal = true;
  }

  confirmRating() {
    this.ratingService
      .rateProduct(this.productId, this.selectedRating)
      .subscribe({
        next: () => {
          this.currentRate = this.selectedRating;
          this.userHasRated = true;
          this.ratingUpdated.emit();
          this.showModal = false;
        },
        error: (err) => {
          alert(err.error?.message || 'Impossible de noter le produit.');
          this.showModal = false;
        },
      });
  }

  cancelRating() {
    this.showModal = false;
  }
}
