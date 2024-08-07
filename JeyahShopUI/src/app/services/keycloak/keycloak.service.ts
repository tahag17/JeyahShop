import {Injectable} from '@angular/core';
import Keycloak from "keycloak-js";

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'JeyahShop',
        clientId: 'jeyah-shop-rest-api'
      });
    }
    return this._keycloak
  }

  constructor() {
  }

  async init() {
    console.log('Authenticating the user...');
    const authenticated = await this.keycloak?.init({
      onLoad: 'login-required'
    })
    console.log('Authenticated!');
  }
}
