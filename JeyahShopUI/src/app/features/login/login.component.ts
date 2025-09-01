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

  loginWithGoogle() {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  }
}
