import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { User } from '../../../shared/models/user.model';
import { map, Observable } from 'rxjs';
import { BackendUser } from '../../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../../utils/map-user.utils';

@Injectable({
  providedIn: 'root',
})
export default class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/users`;

  constructor() {}

  // Generic update (with UpdateUserRequest)
  updateUser(id: number, payload: any): Observable<User> {
    return this.http
      .patch<BackendUser>(`${this.apiUrl}/${id}`, payload, {
        withCredentials: true,
      })
      .pipe(map(mapBackendUserToUser));
  }

  // Update phone
  updatePhone(id: number, phone: string): Observable<User> {
    return this.http
      .patch<BackendUser>(
        `${this.apiUrl}/${id}/phone`,
        { phone },
        { withCredentials: true }
      )
      .pipe(map(mapBackendUserToUser)); // 🔥 always convert
  }

  // Update first name
  updateFirstName(id: number, firstName: string): Observable<User> {
    return this.http
      .patch<BackendUser>(
        `${this.apiUrl}/${id}/first-name`,
        { firstName },
        { withCredentials: true }
      )
      .pipe(map(mapBackendUserToUser));
  }

  // Update last name
  updateLastName(id: number, lastName: string): Observable<User> {
    return this.http
      .patch<BackendUser>(
        `${this.apiUrl}/${id}/last-name`,
        { lastName },
        { withCredentials: true }
      )
      .pipe(map(mapBackendUserToUser));
  }

  // Update address
  updateAddress(
    id: number,
    address: { street: string; city: string; postalCode: number }
  ): Observable<User> {
    console.log('you have reached the service');
    return this.http
      .patch<BackendUser>(`${this.apiUrl}/${id}/address`, address, {
        withCredentials: true,
      })
      .pipe(map(mapBackendUserToUser));
  }

  // Update password
  updatePassword(
    id: number,
    payload: { oldPassword?: string; newPassword: string }
  ): Observable<string> {
    return this.http.patch(`${this.apiUrl}/${id}/password`, payload, {
      responseType: 'text',
      withCredentials: true,
    });
  }
}
