import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { NgIconComponent } from '@ng-icons/core';
import { CommonModule, NgIf } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SearchService } from '../../core/services/search/search.service';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.service';
import { CartService } from '../../core/services/cart/cart.service';
import { Cart } from '../../shared/models/cart.model';
import { Subscription, fromEvent } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    DarkModeToggleComponent,
    NgIconComponent,
    NgIf,
    RouterModule,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  menuOpen = false;
  cartCount = 0;
  searchInput = '';
  notConnectedModalOpen = false;

  profileDropdownOpen = false;
  cartDropdownOpen = false;
  showSearchSuggestions = false;
  filteredSuggestions: string[] = [];

  cart: Cart | null = null;
  private cartModalSub?: Subscription;
  private clickOutsideSub?: Subscription;

  constructor(
    private searchService: SearchService,
    private authService: AuthService,
    private cartService: CartService,
    public router: Router,
    private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    if (this.isLoggedIn) {
      this.loadCart();
    }

    // Listen for cart modal trigger
    this.cartModalSub = this.cartService.cartModal$.subscribe(() => {
      this.cartDropdownOpen = true;
      this.loadCart();
    });

    // Click outside listener
    this.clickOutsideSub = fromEvent(document, 'click').subscribe(
      (event: any) => {
        this.closeDropdownsOnOutsideClick(event);
      }
    );
  }

  ngOnDestroy(): void {
    this.cartModalSub?.unsubscribe();
    this.clickOutsideSub?.unsubscribe();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get isManagerOrAdmin(): boolean {
    return (
      this.authService.hasRole('ROLE_MANAGER') ||
      this.authService.hasRole('ROLE_ADMIN')
    );
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.menuOpen = false;
    this.authService.logout();
  }

  onSearch() {
    if (!this.searchInput.trim()) return;
    this.searchService.setKeyword(this.searchInput);
    this.router.navigate(['/products']);
  }

  handleProfileClick() {
    if (this.isLoggedIn) {
      this.profileDropdownOpen = !this.profileDropdownOpen;
      this.cartDropdownOpen = false;
    } else {
      this.notConnectedModalOpen = true;
    }
  }

  handleCartClick() {
    if (this.isLoggedIn) {
      this.cartDropdownOpen = !this.cartDropdownOpen;
      this.profileDropdownOpen = false;
      this.loadCart();
    } else {
      this.notConnectedModalOpen = true;
    }
  }

  loadCart() {
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.cartCount = cart.items.reduce(
          (acc, item) => acc + item.quantity,
          0
        );
      },
      error: (err) => {
        console.error('Failed to load cart', err);
        this.cart = { id: 0, totalPrice: 0, items: [] };
        this.cartCount = 0;
      },
    });
  }

  selectSuggestion(suggestion: string) {
    this.searchInput = suggestion;
    this.showSearchSuggestions = false;
    this.onSearch();
  }

  hideSuggestions() {
    setTimeout(() => (this.showSearchSuggestions = false), 150);
  }

  private closeDropdownsOnOutsideClick(event: Event) {
    const clickedInside = this.elRef.nativeElement.contains(event.target);
    if (!clickedInside) {
      this.cartDropdownOpen = false;
      this.profileDropdownOpen = false;
      this.menuOpen = false;
    }
  }
}
