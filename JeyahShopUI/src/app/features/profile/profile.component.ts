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
  updateAddress(address: { street: string; city: string; postalCode: number }) {
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

  startEditAddress() {
    this.editingAddress = true;
    this.streetValue = this.authService.currentUser?.address?.street || '';
    this.postalCodeValue =
      this.authService.currentUser?.address?.postalCode || '';
    this.cityValue = this.authService.currentUser?.address?.city || '';
  }
  saveAddress() {
    if (!this.authService.currentUser) return;

    if (!/^[0-9]{4,6}$/.test(this.postalCodeValue)) {
      return;
    }

    const updatedAddress = {
      street: this.streetValue,
      city: this.cityValue,
      postalCode: Number(this.postalCodeValue), // ensure it's a number
    };
    this.userService
      .updateAddress(this.authService.currentUser.id, updatedAddress)
      .subscribe((updatedUser) => {
        console.log('Adresse mise à jour', updatedUser);
        this.authService.setCurrentUser(updatedUser);
        this.editingAddress = false;
      });
  }

  // Phone
  startEditPhone() {
    this.editingPhone = true;
    this.phoneValue = this.authService.currentUser?.phone || '';
  }
  cancel() {
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

  openPasswordModal() {
    console.log('Open password modal');
  }
}
