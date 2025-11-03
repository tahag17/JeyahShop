import { Component } from '@angular/core';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { HlmMenuBarModule } from '@spartan-ng/ui-menu-helm';
import { NgIconComponent } from '@ng-icons/core';
import { NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    DarkModeToggleComponent,
    HlmMenuBarModule,
    NgIconComponent,
    NgIf,
    RouterModule,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent {
  menuOpen = false; // toggles the mobile dropdown
  cartCount = 100;

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.menuOpen = false;
    console.log('Logout clicked!');
    // TODO: call your auth logout function here
  }
}
