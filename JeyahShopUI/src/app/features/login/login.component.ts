import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, NgZone } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { Router } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideAirplay } from '@ng-icons/lucide';

import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { BackendUser } from '../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../utils/map-user.utils';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIcon, DarkModeToggleComponent],
  providers: [provideIcons({ lucideAirplay })],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginForm: FormGroup;
  loginError: string | null = null; // <-- Add this
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private zone = inject(NgZone);
  private backendUrl = environment.apiBaseUrl;

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  login() {
    // reset previous error
    this.loginError = null;

    if (this.loginForm.invalid) {
      // touch all fields to show frontend validation
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (user: User) => {
        this.authService.navigateAfterLogin(user);
      },
      error: (err) => {
        // Extract backend error message
        this.loginError = err.message || 'Erreur inconnue, réessayez';
      },
    });
  }

  loginWithGoogle() {
    const width = 500;
    const height = 600;
    const left = window.innerWidth / 2 - width / 2;
    const top = window.innerHeight / 2 - height / 2;

    // Extract backend origin dynamically from backendUrl
    const backendUrlObj = new URL(this.backendUrl); // e.g., http://localhost:8080 or https://jeyahshop.onrender.com
    const backendOrigin = `${backendUrlObj.protocol}//${backendUrlObj.host}`;

    // Add listener BEFORE opening popup
    const listener = (event: MessageEvent) => {
      console.log(
        'Message received from:',
        event.origin,
        'expected backend:',
        backendOrigin
      );

      // Robust origin check: make sure the message comes from the backend
      if (event.origin !== backendOrigin) {
        console.warn('Rejected message from', event.origin);
        return;
      }

      const backendUser: BackendUser = event.data;

      // Convert backend user to frontend user
      const user: User = this.authService.processBackendUser(backendUser);
      console.log('Google login user:', user);
      console.log(
        'creationDate prototype:',
        Object.getPrototypeOf(user.creationDate)
      );
      console.log(
        'lastModifiedDate prototype:',
        Object.getPrototypeOf(user.lastModifiedDate)
      );

      // Navigate after login
      this.authService.navigateAfterLogin(user);

      // Remove listener after handling
      window.removeEventListener('message', listener);
    };

    window.addEventListener('message', listener);

    // Open OAuth2 popup
    const popup = window.open(
      `${backendOrigin}/oauth2/authorization/google`,
      'google-login',
      `width=${width},height=${height},top=${top},left=${left}`
    );

    if (!popup) console.error('Popup blocked by browser');
  }
}
