import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { User } from '../../../shared/models/user.model';
import { BehaviorSubject, catchError, map, tap, throwError } from 'rxjs';
import { convertDateArrayToDate } from '../../../utils/date.utils';
import { BackendUser } from '../../../shared/models/backend-user.model';
import { mapBackendUserToUser } from '../../../utils/map-user.utils';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(this.platformId)) {
      const storedUser = localStorage.getItem('currentUser');
      if (storedUser) {
        const backendUser = JSON.parse(storedUser);
        const user = mapBackendUserToUser(backendUser);
        this.currentUserSubject.next(user);
      }
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
        map((backendUser) => this.handleLoginSuccess(backendUser)),
        tap((user) => {
          this.currentUserSubject.next(user);
          localStorage.setItem('currentUser', JSON.stringify(user));
        }),
        catchError((err) => {
          // Extract backend message
          const backendMessage =
            err.error?.message ||
            err.error?.error ||
            'Erreur inconnue, veuillez réessayer';
          console.error('Login error:', backendMessage);
          return throwError(() => new Error(backendMessage)); // rethrow as observable error
        })
      );
  }

  logout() {
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
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

  hasRole(role: string): boolean {
    const user = this.currentUser;
    if (!user || !user.roles) return false;
    return user.roles.includes(role);
  }
}
