import { Component, inject, OnInit } from '@angular/core';
import UserService from '../../core/services/user/user.service';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { CommonModule, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [NgIf, CommonModule, FormsModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
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

  // --- Edit Modal ---
  isEditUserModalOpen = false;
  postalCodeErrorUserUpdate: string | null = null;

  editUserId: number | null = null;
  editFirstName = '';
  editLastName = '';
  editPhone = '';
  editStreet = '';
  editCity = '';
  editPostalCode = '';

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

  // --- Toggle enable/disable ---
  toggleUserStatus(user: User) {
    this.userService.toggleUserEnabled(user.id).subscribe({
      next: (updated) => (user.enabled = updated.enabled),
      error: (err) => console.error('Erreur changement statut:', err),
    });
  }

  // --- Delete user ---
  deleteUser(userId: number) {
    if (!confirm('Voulez-vous vraiment supprimer cet utilisateur ?')) return;
    this.userService.deleteUser(userId).subscribe({
      next: () => (this.users = this.users.filter((u) => u.id !== userId)),
      error: (err) => console.error('Erreur suppression utilisateur:', err),
    });
  }

  // --- Open Modal ---
  openEditUserModal(user: User) {
    this.isEditUserModalOpen = true;
    this.editUserId = user.id;

    this.editFirstName = user.firstName || '';
    this.editLastName = user.lastName || '';
    this.editPhone = user.phone || '';
    this.editStreet = user.address?.street || '';
    this.editCity = user.address?.city || '';
    this.editPostalCode = user.address?.postalCode || '';
  }

  closeEditUserModal() {
    this.isEditUserModalOpen = false;
    this.postalCodeErrorUserUpdate = null;
  }

  saveUserEdits() {
    if (!this.editUserId) return; // check if a user is being edited

    // Flatten address directly into payload
    const payload = {
      firstName: this.editFirstName,
      lastName: this.editLastName,
      phone: this.editPhone,
      street: this.editStreet,
      city: this.editCity,
      postalCode: this.editPostalCode,
    };

    this.userService.updateUserAsManager(this.editUserId, payload).subscribe({
      next: (updatedUser) => {
        console.log('Utilisateur mis à jour', updatedUser);

        // Update table with the latest user info
        const index = this.users.findIndex((u) => u.id === updatedUser.id);
        if (index > -1) this.users[index] = updatedUser;

        this.closeEditUserModal();
        this.postalCodeErrorUserUpdate = null;
      },
      error: (err) => {
        if (err.status === 400 && err.error?.message) {
          this.postalCodeErrorUserUpdate = err.error.message;
        } else if (err.status === 403 && err.error?.message) {
          // Manager trying to edit forbidden user
          this.postalCodeErrorUserUpdate = err.error.message;
        } else {
          this.postalCodeErrorUserUpdate =
            'Erreur inconnue, veuillez réessayer';
        }
      },
    });
  }
}
