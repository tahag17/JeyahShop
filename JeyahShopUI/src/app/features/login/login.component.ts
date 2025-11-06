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

// import { HlmInput } from '@spartan-ng/helm/input';
import { HlmInputModule } from '@spartan-ng/ui-input-helm';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';
import { BackendUser } from '../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../utils/map-user.utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIcon,
    HlmInputModule,
    DarkModeToggleComponent,
  ],
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

  // loginWithGoogle() {
  //   const width = 500;
  //   const height = 600;
  //   const left = window.innerWidth / 2 - width / 2;
  //   const top = window.innerHeight / 2 - height / 2;

  //   const popup = window.open(
  //     'http://localhost:8080/oauth2/authorization/google',
  //     'google-login',
  //     `width=${width},height=${height},top=${top},left=${left}`
  //   );

  //   // Listen for messages from the popup
  //   const listener = (event: MessageEvent) => {
  //     if (event.origin !== 'http://localhost:8080') return; // security check

  //     const user: User = event.data;
  //     console.log('OAuth2 user:', user);

  //     this.authService.setCurrentUser(user); // use the public method

  //     if (user.roles.includes('ROLE_MANAGER')) {
  //       this.router.navigate(['/dashboard']);
  //     } else if (user.roles.includes('ROLE_USER')) {
  //       this.router.navigate(['/store']);
  //     } else {
  //       this.router.navigate(['/']);
  //     }

  //     window.removeEventListener('message', listener); // cleanup
  //   };

  //   window.addEventListener('message', listener);
  // }

  loginWithGoogle() {
    const width = 500;
    const height = 600;
    const left = window.innerWidth / 2 - width / 2;
    const top = window.innerHeight / 2 - height / 2;

    // Add listener BEFORE opening popup
    const listener = (event: MessageEvent) => {
      if (event.origin !== 'http://localhost:8080') return;

      const backendUser: BackendUser = event.data;

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

      this.authService.navigateAfterLogin(user);

      window.removeEventListener('message', listener);
    };

    window.addEventListener('message', listener);

    const popup = window.open(
      'http://localhost:8080/oauth2/authorization/google',
      'google-login',
      `width=${width},height=${height},top=${top},left=${left}`
    );

    if (!popup) console.error('Popup blocked by browser');
  }
}
