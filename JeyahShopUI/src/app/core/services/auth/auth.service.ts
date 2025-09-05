import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { User } from '../../../shared/models/user.model';
import { BehaviorSubject, map, tap } from 'rxjs';
import { convertDateArrayToDate } from '../../../utils/date.utils';
import { BackendUser } from '../../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../../utils/map-user.utils';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {
    // const storedUser = localStorage.getItem('currentUser');
    // if (storedUser) {
    //   const backendUser = JSON.parse(storedUser); // stored in backend format
    //   this.currentUserSubject.next(mapBackendUserToUser(backendUser));
    // }
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      const backendUser = JSON.parse(storedUser); // stored in backend format
      const user = mapBackendUserToUser(backendUser);
      this.currentUserSubject.next(user);
    }
  }

  private http = inject(HttpClient);
  private loginUrl = `${environment.apiBaseUrl}public/api/auth/login`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private router = inject(Router);

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Optional: Observable for components to subscribe to
  get currentUser$(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  login(credentials: { email: string; password: string }) {
    return this.http
      .post<BackendUser>(this.loginUrl, credentials, { withCredentials: true })
      .pipe(
        map((backendUser) => this.handleLoginSuccess(backendUser)), // <-- use mapper
        tap((user) => {
          this.currentUserSubject.next(user);
          localStorage.setItem('currentUser', JSON.stringify(user));
        })
      );
  }

  logout() {
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
  }

  setCurrentUser(user: User) {
    this.currentUserSubject.next(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  private handleLoginSuccess(backendUser: BackendUser) {
    const user = mapBackendUserToUser(backendUser);
    this.setCurrentUser(user);
    return user;
    // ✅ Add these logs here
    // console.log('Mapped user:', user);
    // console.log(
    //   'creationDate:',
    //   user.creationDate,
    //   user.creationDate?.getTime()
    // );
    // console.log(
    //   'lastModifiedDate:',
    //   user.lastModifiedDate,
    //   user.lastModifiedDate?.getTime()
    // );
  }

  processBackendUser(backendUser: BackendUser): User {
    return this.handleLoginSuccess(backendUser);
  }

  navigateAfterLogin(user: User) {
    if (user.roles.includes('ROLE_MANAGER')) {
      this.router.navigate(['/dashboard/profile']);
    } else if (user.roles.includes('ROLE_USER')) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/']);
    }
  }
}
