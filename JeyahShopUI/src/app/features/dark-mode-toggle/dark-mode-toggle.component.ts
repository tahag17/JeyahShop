import { Component, AfterViewInit } from '@angular/core';
import { NgIf } from '@angular/common';
import { ThemeService } from '../../core/services/theme/theme.service';
@Component({
  selector: 'app-dark-mode-toggle',
  templateUrl: './dark-mode-toggle.component.html',
  styleUrls: ['./dark-mode-toggle.component.scss'],
  imports: [NgIf],
  standalone: true,
})
// implements AfterViewInit
export class DarkModeToggleComponent {
  // isDarkMode = false;

  // ngAfterViewInit() {
  //   // Only runs in the browser after view is initialized
  //   if (typeof window !== 'undefined') {
  //     const savedMode = localStorage.getItem('dark-mode');
  //     if (savedMode === 'true') {
  //       this.enableDarkMode();
  //       this.isDarkMode = true;
  //     }
  //   }
  // }

  // toggleDarkMode() {
  //   if (typeof window !== 'undefined') {
  //     this.isDarkMode = !this.isDarkMode;
  //     if (this.isDarkMode) {
  //       this.enableDarkMode();
  //     } else {
  //       this.disableDarkMode();
  //     }
  //   }
  // }

  // private enableDarkMode() {
  //   document.documentElement.classList.add('dark');
  //   localStorage.setItem('dark-mode', 'true');
  // }

  // private disableDarkMode() {
  //   document.documentElement.classList.remove('dark');
  //   localStorage.setItem('dark-mode', 'false');
  // }

  constructor(public themeService: ThemeService) {}

  toggleDarkMode() {
    this.themeService.toggleDark();
  }

  get isDarkMode() {
    return this.themeService.theme === 'dark';
  }
}
