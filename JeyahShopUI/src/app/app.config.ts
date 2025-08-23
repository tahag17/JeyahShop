import {
  APP_INITIALIZER,
  ApplicationConfig,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
// import {KeycloakService} from "./services/keycloak/keycloak.service";
import { HttpClient } from '@angular/common/http';

// export function kcFactory(kcService: KeycloakService) {
//   return () => kcService.init();
//   // return () => true;

// }

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),

    provideRouter(routes),

    provideClientHydration(),

    //,
    // {
    //   provide: APP_INITIALIZER,
    //   deps: [KeycloakService],
    //   useFactory: kcFactory,
    //   multi: true
    // }
    HttpClient,
  ],
};
