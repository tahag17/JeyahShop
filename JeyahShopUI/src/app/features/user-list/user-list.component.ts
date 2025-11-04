import { Component, inject, OnInit } from '@angular/core';
import UserService from '../../core/services/user/user.service';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [NgIf, CommonModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  authService = inject(AuthService);

  users: User[] = [];
  loading = true;
  error: string | null = null;

  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(page = 0) {
    this.loading = true;
    this.userService.getAllUsers(page).subscribe({
      next: (data) => {
        this.users = data.users;
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error = 'Erreur lors du chargement des utilisateurs.';
        this.loading = false;
      },
    });
  }

  toggleUserStatus(user: User) {
    const newStatus = !user.enabled;
    this.userService.setUserEnabled(user.id, newStatus).subscribe({
      next: (updated) => {
        user.enabled = updated.enabled;
      },
      error: (err) => {
        console.error('Erreur changement statut:', err);
      },
    });
  }

  deleteUser(userId: number) {
    if (!confirm('Voulez-vous vraiment supprimer cet utilisateur ?')) return;
    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.users = this.users.filter((u) => u.id !== userId);
      },
      error: (err) => {
        console.error('Erreur suppression utilisateur:', err);
      },
    });
  }
}
