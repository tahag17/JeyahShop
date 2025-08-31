import { Component, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-dark-mode-toggle',
  templateUrl: './dark-mode-toggle.component.html',
  styleUrls: ['./dark-mode-toggle.component.scss'],
  standalone: true,
})
export class DarkModeToggleComponent implements AfterViewInit {
  isDarkMode = false;

  ngAfterViewInit() {
    // Only runs in the browser after view is initialized
    if (typeof window !== 'undefined') {
      const savedMode = localStorage.getItem('dark-mode');
      if (savedMode === 'true') {
        this.enableDarkMode();
        this.isDarkMode = true;
      }
    }
  }

  toggleDarkMode() {
    if (typeof window !== 'undefined') {
      this.isDarkMode = !this.isDarkMode;
      if (this.isDarkMode) {
        this.enableDarkMode();
      } else {
        this.disableDarkMode();
      }
    }
  }

  private enableDarkMode() {
    document.documentElement.classList.add('dark');
    localStorage.setItem('dark-mode', 'true');
  }

  private disableDarkMode() {
    document.documentElement.classList.remove('dark');
    localStorage.setItem('dark-mode', 'false');
  }
}
