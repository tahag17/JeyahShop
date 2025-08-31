import { Component } from '@angular/core';
import { ServicesComponent } from '../services/services.component';
import { RouterLink } from '@angular/router';
import { DarkModeToggleComponent } from '../../shared/dark-mode-toggle/dark-mode-toggle.component';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [RouterLink, DarkModeToggleComponent],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.scss',
})
export class WelcomeComponent {}
