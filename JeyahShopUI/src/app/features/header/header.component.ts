import { Component } from '@angular/core';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { NgIconComponent } from '@ng-icons/core';
import { NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SearchService } from '../../core/services/search/search.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    DarkModeToggleComponent,
    NgIconComponent,
    NgIf,
    RouterModule,
    FormsModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  menuOpen = false; // toggles the mobile dropdown
  cartCount = 100;
  searchInput = ''; // two-way bound to input

  constructor(private searchService: SearchService) {}

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.menuOpen = false;
    console.log('Logout clicked!');
    // TODO: call your auth logout function here
  }

  onSearch() {
    this.searchService.setKeyword(this.searchInput);
  }
}
