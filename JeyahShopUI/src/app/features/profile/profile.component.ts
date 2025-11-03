import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth/auth.service';
import { User } from '../../shared/models/user.model';
import { CommonModule, DatePipe, NgIf } from '@angular/common';
import { AsyncPipe } from '@angular/common';
import UserService from '../../core/services/user/user.service';
import { FormsModule } from '@angular/forms';
import { convertDateArrayToDate } from '../../utils/date.utils';
import { tap } from 'rxjs';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf, AsyncPipe, DatePipe, CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  // way 1: snapshot value

  // way 2: observable (preferred if it can change dynamically)
  user$ = this.authService.currentUser$.pipe(
    tap((user) => {
      if (user) {
        console.log('ProfileComponent user$ emitted:', user);
        console.log(
          'creationDate instanceof Date?',
          user.creationDate instanceof Date
        );
        console.log(
          'lastModifiedDate instanceof Date?',
          user.lastModifiedDate instanceof Date
        );
        console.log(
          'creationDate prototype:',
          Object.getPrototypeOf(user.creationDate)
        );
      }
    })
  );

  updatePhone(phone: string) {
    const user = this.authService.currentUser;
    if (user) {
      this.userService.updatePhone(user.id, phone).subscribe((updated) => {
        console.log('Téléphone mis à jour', updated);
      });
    }
  }

  updatePassword(oldPassword: string, newPassword: string) {
    const user = this.authService.currentUser;
    if (user) {
      this.userService
        .updatePassword(user.id, { oldPassword, newPassword })
        .subscribe({
          next: (msg) => console.log(msg),
          error: (err) => console.error('Erreur :', err),
        });
    }
  }

  // ✅ Update first name
  updateFirstName(firstName: string) {
    const user = this.authService.currentUser;
    if (user) {
      this.userService
        .updateFirstName(user.id, firstName)
        .subscribe((updated) => {
          console.log('Prénom mis à jour', updated);
        });
    }
  }

  // ✅ Update last name
  updateLastName(lastName: string) {
    const user = this.authService.currentUser;
    if (user) {
      this.userService
        .updateLastName(user.id, lastName)
        .subscribe((updated) => {
          console.log('Nom mis à jour', updated);
        });
    }
  }

  // ✅ Update address
  updateAddress(address: { street: string; city: string; postalCode: string }) {
    const user = this.authService.currentUser;
    if (user) {
      this.userService.updateAddress(user.id, address).subscribe((updated) => {
        console.log('Adresse mise à jour', updated);
        this.authService.setCurrentUser(updated); // 🔥 update observable + localStorage
      });
    }
  }

  // Inline editing flags & values
  editingPhone = false;
  phoneValue = '';

  editingFirstName = false;
  firstNameValue = '';

  editingLastName = false;
  lastNameValue = '';

  editingAddress = false;
  streetValue = '';
  postalCodeValue = '';
  cityValue = '';
  postalCodeError: string | null = null;

  startEditAddress() {
    this.editingAddress = true;
    this.streetValue = this.authService.currentUser?.address?.street || '';
    this.postalCodeValue =
      this.authService.currentUser?.address?.postalCode || '';
    this.cityValue = this.authService.currentUser?.address?.city || '';
  }
  cancelEditAddress() {
    this.editingAddress = false;
    this.postalCodeError = null;
  }
  saveAddress() {
    if (!this.authService.currentUser) return;

    const updatedAddress = {
      street: this.streetValue,
      city: this.cityValue,
      postalCode: this.postalCodeValue,
    };
    this.userService
      .updateAddress(this.authService.currentUser.id, updatedAddress)
      .subscribe({
        next: (updatedUser) => {
          console.log('Adresse mise à jour', updatedUser);
          this.authService.setCurrentUser(updatedUser);
          this.editingAddress = false;
          this.postalCodeError = null; // clear error
        },
        error: (err) => {
          if (err.status === 400 && err.error?.message) {
            this.postalCodeError = err.error.message; // show backend error
          } else {
            this.postalCodeError = 'Erreur inconnue, veuillez réessayer';
          }
        },
      });
  }

  // Phone
  startEditPhone() {
    this.editingPhone = true;
    this.phoneValue = this.authService.currentUser?.phone || '';
  }
  cancelPhoneEdit() {
    this.editingPhone = false;
  }
  savePhone() {
    if (!this.authService.currentUser) return;
    this.userService
      .updatePhone(this.authService.currentUser.id, this.phoneValue)
      .subscribe((updated) => {
        console.log('Téléphone mis à jour', updated);
        this.authService.setCurrentUser(updated); // 🔥 update observable + localStorage
        this.editingPhone = false;
      });
  }

  // First Name
  startEditFirstName() {
    this.editingFirstName = true;
    this.firstNameValue = this.authService.currentUser?.firstName || '';
  }
  cancelFirstNameEdit() {
    this.editingFirstName = false;
  }
  saveFirstName() {
    if (!this.authService.currentUser) return;
    this.userService
      .updateFirstName(this.authService.currentUser.id, this.firstNameValue)
      .subscribe((updated) => {
        console.log('Prénom mis à jour', updated);
        this.authService.setCurrentUser(updated); // 🔥 update observable + localStorage
        this.editingFirstName = false;
      });
  }

  // Last Name
  startEditLastName() {
    this.editingLastName = true;
    this.lastNameValue = this.authService.currentUser?.lastName || '';
  }
  cancelLastNameEdit() {
    this.editingLastName = false;
  }
  saveLastName() {
    if (!this.authService.currentUser) return;
    this.userService
      .updateLastName(this.authService.currentUser.id, this.lastNameValue)
      .subscribe((updated) => {
        console.log('Nom mis à jour', updated);
        this.authService.setCurrentUser(updated); // 🔥 update observable + localStorage
        this.editingLastName = false;
      });
  }

  // Modal state
  isPasswordModalOpen = false;
  isSettingPassword = false;

  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  passwordError: string | null = null; // new property

  openPasswordModal(user: User) {
    this.isPasswordModalOpen = true;
    this.isSettingPassword = !user.hasPassword; // true if first-time setup
    this.oldPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
  }

  closePasswordModal() {
    this.isPasswordModalOpen = false;
  }
  savePassword() {
    if (this.newPassword !== this.confirmPassword) {
      console.error('Les mots de passe ne correspondent pas');
      return;
    }

    const user = this.authService.currentUser;
    if (!user) return;

    const payload = this.isSettingPassword
      ? { newPassword: this.newPassword } // oldPassword omitted
      : { oldPassword: this.oldPassword, newPassword: this.newPassword };

    this.userService.updatePassword(user.id, payload).subscribe({
      next: (msg) => {
        console.log(msg);
        // 🔥 Update observable so the UI knows the user now has a password
        if (this.isSettingPassword) {
          this.authService.setCurrentUser({
            ...user,
            hasPassword: true, // mark that the password is now set
          });
        }
        this.closePasswordModal();
      },
      error: (err) => {
        // Check for BadCredentialsException (backend 400/401)
        if (err.status === 401) {
          this.passwordError = 'Ancien mot de passe incorrect';
        } else if (err.status === 400 && err.error?.message) {
          this.passwordError = err.error.message;
        } else {
          this.passwordError = 'Erreur inconnue, veuillez réessayer';
        }
      },
    });
  }

  // Modal state
  isEditUserModalOpen = false;

  // Form values
  editFirstName = '';
  editLastName = '';
  editPhone = '';
  editStreet = '';
  editCity = '';
  editPostalCode = '';
  postalCodeErrorUserUpdate: String | null = null;

  openEditUserModal(user: User) {
    this.isEditUserModalOpen = true;

    // Pre-fill all fields
    this.editFirstName = user.firstName || '';
    this.editLastName = user.lastName || '';
    this.editPhone = user.phone || '';
    this.editStreet = user.address?.street || '';
    this.editCity = user.address?.city || '';
    this.editPostalCode = user.address?.postalCode?.toString() || '';
  }

  closeEditUserModal() {
    this.isEditUserModalOpen = false;
    this.postalCodeErrorUserUpdate = null;
  }

  saveUserEdits() {
    const user = this.authService.currentUser;
    if (!user) return;

    const payload = {
      firstName: this.editFirstName,
      lastName: this.editLastName,
      phone: this.editPhone,
      street: this.editStreet,
      city: this.editCity,
      postalCode: this.editPostalCode,
    };

    this.userService.updateUser(user.id, payload).subscribe({
      next: (updatedUser) => {
        console.log('Utilisateur mis à jour', updatedUser);
        this.authService.setCurrentUser(updatedUser); // 🔥 update observable + UI
        this.closeEditUserModal();
        this.postalCodeErrorUserUpdate = null;
      },
      error: (err) => {
        if (err.status === 400 && err.error?.message) {
          // 🚨 backend validation message (from IllegalArgumentException)
          this.postalCodeErrorUserUpdate = err.error.message;
        } else {
          this.postalCodeErrorUserUpdate =
            'Erreur inconnue, veuillez réessayer';
        }
      },
    });
  }
}
