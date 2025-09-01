import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { DarkModeToggleComponent } from './features/dark-mode-toggle/dark-mode-toggle.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, DarkModeToggleComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'JeyahShopUI';
}
