import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {KeycloakService} from "../keycloak/keycloak.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate{
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const url: string = state.url;

    // Allow access if the route starts with '/public'
    if (url.startsWith('/public')) {
      return true;
    }

    // For other routes, check if the user is authenticated
    if (!this.keycloakService.keycloak?.authenticated) {
      // If not authenticated, initiate login and return false to block access
      this.keycloakService.login();
      return false;
    }

    // If authenticated, allow access
    return true;
  }
}
