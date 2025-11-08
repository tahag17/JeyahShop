import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private currentTheme: 'light' | 'dark' | 'legacy' = 'light';

  constructor() {
    const savedTheme = localStorage.getItem('theme') as
      | 'light'
      | 'dark'
      | 'legacy'
      | null;
    if (savedTheme) {
      this.setTheme(savedTheme);
    }
  }

  get theme() {
    return this.currentTheme;
  }

  setTheme(theme: 'light' | 'dark' | 'legacy') {
    document.documentElement.classList.remove('dark', 'legacy');
    this.currentTheme = theme;

    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else if (theme === 'legacy') {
      document.documentElement.classList.add('legacy');
    }

    localStorage.setItem('theme', theme);
  }

  toggleDark() {
    this.setTheme(this.currentTheme === 'dark' ? 'light' : 'dark');
  }

  toggleLegacy() {
    this.setTheme(this.currentTheme === 'legacy' ? 'light' : 'legacy');
  }
}
