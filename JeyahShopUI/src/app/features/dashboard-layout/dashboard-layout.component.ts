import { Component, HostListener } from '@angular/core';
import {
  Router,
  RouterOutlet,
  RouterLink,
  RouterLinkActive,
} from '@angular/router';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { CommonModule, NgIf } from '@angular/common';
import { AuthService } from '../../core/services/auth/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive, // ✅ ADD THESE
    DarkModeToggleComponent,
    NgIf,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.scss',
})
export class DashboardLayoutComponent {
  sidebarOpen = false;

  constructor(private router: Router, private authService: AuthService) {}

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  navigate(path: string) {
    this.router.navigate([path]);
    this.sidebarOpen = false; // close overlay on mobile
  }

  logout() {
    this.authService.logout();
  }

  // Search bar state
  searchInput = '';
  showSearchSuggestions = false;
  filteredSuggestions: string[] = [];
  allSuggestions: string[] = [
    'Produit 1',
    'Produit 2',
    'Produit 3',
    'Produit 4',
  ]; // replace with actual product names or fetch from API

  // Called on input in search bar
  onSearch() {
    const query = this.searchInput.trim().toLowerCase();
    if (!query) {
      this.filteredSuggestions = [];
      return;
    }

    // Filter suggestions (you can replace with API call)
    this.filteredSuggestions = this.allSuggestions.filter((item) =>
      item.toLowerCase().includes(query)
    );
  }

  // Hide suggestions after blur
  hideSuggestions() {
    // Delay to allow click on suggestion before hiding
    setTimeout(() => (this.showSearchSuggestions = false), 150);
  }

  // Select a suggestion
  selectSuggestion(suggestion: string) {
    this.searchInput = suggestion;
    this.showSearchSuggestions = false;

    // Navigate to search results page if needed
    this.router.navigate(['/products'], {
      queryParams: { search: suggestion },
    });
  }
}
