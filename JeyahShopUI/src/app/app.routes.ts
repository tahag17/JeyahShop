import {Routes} from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {AppComponent} from "./app.component";
import {AuthGuardService} from "./services/guard/auth-guard.service";
import {WelcomeComponent} from "./pages/welcome/welcome.component";
import {AboutComponent} from "./pages/about/about.component";
import {ServicesComponent} from "./pages/services/services.component";
import {ContactComponent} from "./pages/contact/contact.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [AuthGuardService]
  },
  {
    path: '',
    redirectTo: 'public/welcome',
    pathMatch: 'full'
  },
  {path: 'public/welcome', component: WelcomeComponent},
  {path: 'public/about', component: AboutComponent},
  {path: 'public/services', component: ServicesComponent},
  {path: 'public/contact', component: ContactComponent},
];
