import { Component, OnInit } from '@angular/core';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { NgIconComponent } from '@ng-icons/core';
import { CommonModule, NgIf } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SearchService } from '../../core/services/search/search.service';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.service';
import { CartService } from '../../core/services/cart/cart.service';
import { CartItem } from '../../shared/models/cart-item.model';
import { Cart } from '../../shared/models/cart.model';

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
export class HeaderComponent implements OnInit {
  menuOpen = false;
  cartCount = 0;
  searchInput = '';
  notConnectedModalOpen = false;

  profileDropdownOpen = false;
  cartDropdownOpen = false;
  showSearchSuggestions = false;
  filteredSuggestions: string[] = [];

  cart: Cart | null = null;

  constructor(
    private searchService: SearchService,
    private authService: AuthService,
    private cartService: CartService,
    public router: Router
  ) {}

  ngOnInit(): void {
    if (this.isLoggedIn) {
      this.loadCart();
    }
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

    // set the keyword in the search service
    this.searchService.setKeyword(this.searchInput);

    // navigate to the products list page
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
      this.loadCart(); // refresh cart on open
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
}
