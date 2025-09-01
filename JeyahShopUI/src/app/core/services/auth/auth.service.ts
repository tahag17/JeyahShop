import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { User } from '../../../shared/models/user.model';
import { BehaviorSubject, map, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {}
  private http = inject(HttpClient);
  private loginUrl = `${environment.apiBaseUrl}public/api/auth/login`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Optional: Observable for components to subscribe to
  get currentUser$(): Observable<User | null> {
    return this.currentUserSubject.asObservable();
  }

  login(credentials: { email: string; password: string }): Observable<User> {
    return this.http
      .post<User>(this.loginUrl, credentials, { withCredentials: true })
      .pipe(
        map((user) => ({
          ...user,
          creationDate: new Date(user.creationDate).toISOString(),
          lastModifiedDate: user.lastModifiedDate
            ? new Date(user.lastModifiedDate).toISOString()
            : null,
        })),
        tap((user) => this.currentUserSubject.next(user))
      );
  }

  logout() {
    this.currentUserSubject.next(null);
    // Optionally clear session/cookies here
  }
}
