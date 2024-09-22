import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from '../keycloak/keycloak.service';

@Injectable()
export class HttpTokenInterceptor implements HttpInterceptor {

  constructor(private keycloakService: KeycloakService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {

    // Skip token addition for public URLs
    const isPublicUrl = request.url.includes('/public');
    if (isPublicUrl) {
      return next.handle(request);
    }

    // Retrieve token from Keycloak service
    const token = this.keycloakService.keycloak?.token;

    if (token) {
      // Clone the request and add the Authorization header while keeping other headers intact
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(authReq);
    }

    // If no token is present, proceed without modifying the request
    console.warn('No token found, request sent without Authorization header');
    return next.handle(request);
  }
}
