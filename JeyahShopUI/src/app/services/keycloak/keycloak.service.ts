import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from './user-profile';

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;
  private _profile: UserProfile | undefined;

  get keycloak() {
    if (!this._keycloak && typeof window !== 'undefined' && typeof document !== 'undefined') {
      // Only initialize Keycloak in the browser
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'JeyahShop',
        clientId: 'jeyah-shop-rest-api',
      });
    } else if (typeof window === 'undefined' || typeof document === 'undefined') {
      console.warn('Keycloak cannot be initialized outside of the browser.');
    }
    return this._keycloak;
  }

  constructor() {}

  get profile(): UserProfile | undefined {
    return this._profile;
  }

  async init() {
    if (this.keycloak) {
      console.log('Authenticating the user...');
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',
      });
      if (authenticated) {
        console.log('Authenticated!');
        this._profile = (await this.keycloak.loadUserProfile()) as UserProfile;
        this._profile.token = this.keycloak.token;
      }
    }
  }

  async login() {
    return this.keycloak?.login();
  }

  logout() {
    return this.keycloak?.logout();
  }
}
