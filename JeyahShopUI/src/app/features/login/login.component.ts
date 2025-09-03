import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { Router } from '@angular/router';
import {
  HlmIcon,
  HlmIconModule,
  provideHlmIconConfig,
} from '@spartan-ng/helm/icon';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideAirplay } from '@ng-icons/lucide';

// import { HlmInput } from '@spartan-ng/helm/input';
import { HlmInputModule } from '@spartan-ng/ui-input-helm';
import { DarkModeToggleComponent } from '../dark-mode-toggle/dark-mode-toggle.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIcon,
    HlmIconModule,
    HlmInputModule,
    DarkModeToggleComponent,
  ],
  providers: [provideHlmIconConfig({}), provideIcons({ lucideAirplay })],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginForm: FormGroup;
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  login() {
    this.authService.login(this.loginForm.value).subscribe({
      next: (user: User) => {
        console.log('Logged in user:', user);
        if (user.roles.includes('ROLE_MANAGER')) {
          this.router.navigate(['/dashboard']); // manager dashboard
        } else if (user.roles.includes('ROLE_USER')) {
          this.router.navigate(['/store']); // store layout
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => console.error('Login failed', err),
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

      const user: User = event.data;
      console.log('OAuth2 user received:', user);

      this.authService.setCurrentUser(user);

      if (user.roles.includes('ROLE_MANAGER')) {
        this.router.navigate(['/dashboard']);
      } else if (user.roles.includes('ROLE_USER')) {
        this.router.navigate(['/store']);
      } else {
        this.router.navigate(['/']);
      }

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
