import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { CommonModule, DatePipe, NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf, AsyncPipe, DatePipe, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  private authService = inject(AuthService);

  // way 1: snapshot value
  user: User | null = this.authService.currentUser;

  // way 2: observable (preferred if it can change dynamically)
  user$ = this.authService.currentUser$;
}
