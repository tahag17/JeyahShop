import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.currentUser;

  if (!user) {
    // Not logged in → redirect to login
    return router.parseUrl('/login');
  }

  const allowedRoles = route.data['roles'] as string[] | undefined;
  if (allowedRoles && !user.roles.some((role) => allowedRoles.includes(role))) {
    // User doesn't have the required role → redirect home
    return router.parseUrl('/');
  }

  return true;
};
