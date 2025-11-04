import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { User } from '../../../shared/models/user.model';
import { map, Observable } from 'rxjs';
import { BackendUser } from '../../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../../utils/map-user.utils';
import { PaginatedUsers } from '../../../shared/models/paginated-users';

@Injectable({
  providedIn: 'root',
})
export default class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBaseUrl}user/api/users`;
  private managerApiUrl = `${environment.apiBaseUrl}manager/api/users`;

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
    address: { street: string; city: string; postalCode: string }
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
  ): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/password`, payload, {
      withCredentials: true,
    });
  }

  //Manager+ Methods

  getAllUsers(page = 0, size = 10): Observable<PaginatedUsers> {
    return this.http.get<PaginatedUsers>(
      `${this.managerApiUrl}?page=${page}&size=${size}`,
      { withCredentials: true }
    );
  }

  // --- TOGGLE USER ENABLED ---
  toggleUserEnabled(id: number): Observable<User> {
    return this.http
      .patch<BackendUser>(
        `${this.managerApiUrl}/${id}/toggle`,
        {},
        { withCredentials: true }
      )
      .pipe(map(mapBackendUserToUser));
  }

  // --- DELETE USER ---
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.managerApiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  // --- GENERIC UPDATE USER ---
  updateUserAsManager(userId: number, payload: any): Observable<User> {
    return this.http.patch<User>(`${this.managerApiUrl}/${userId}`, payload, {
      withCredentials: true,
    });
  }

  //Admin only method:
  toggleManagerRole(userId: number, makeManager: boolean): Observable<User> {
    return this.http
      .patch<BackendUser>(
        `${this.managerApiUrl}/${userId}/role/manager`,
        { manager: makeManager },
        { withCredentials: true }
      )
      .pipe(map(mapBackendUserToUser));
  }
}
