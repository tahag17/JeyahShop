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
  constructor() {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }
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

  login(credentials: { email: string; password: string }) {
    return this.http
      .post<User>(this.loginUrl, credentials, { withCredentials: true })
      .pipe(
        map((user) => ({ ...user })),
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
}
