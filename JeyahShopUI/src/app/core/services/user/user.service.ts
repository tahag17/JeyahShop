import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { User } from '../../../shared/models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export default class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/users`;

  constructor() {}

  // Generic update (with UpdateUserRequest)
  updateUser(id: number, payload: any): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}`, payload);
  }

  // Update phone
  updatePhone(id: number, phone: string): Observable<User> {
    return this.http.patch<User>(
      `${this.apiUrl}/${id}/phone`,
      { phone },
      { withCredentials: true }
    );
  }

  // Update first name
  updateFirstName(id: number, firstName: string): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/first-name`, {
      firstName,
    });
  }

  // Update last name
  updateLastName(id: number, lastName: string): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/last-name`, {
      lastName,
    });
  }

  // Update address
  updateAddress(
    id: number,
    address: { street: string; city: string; postalCode: number }
  ): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/address`, address);
  }

  // Update password
  updatePassword(
    id: number,
    payload: { oldPassword: string; newPassword: string }
  ): Observable<string> {
    return this.http.patch(`${this.apiUrl}/${id}/password`, payload, {
      responseType: 'text', // because controller returns String
    });
  }
}
