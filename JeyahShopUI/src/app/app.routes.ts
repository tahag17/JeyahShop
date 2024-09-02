import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";
import {AppComponent} from "./app.component";
import {WelcomeComponent} from "./pages/welcome/welcome.component";

export const routes: Routes = [
  {
    path:'login',
    component: LoginComponent
  },
  {
    path:'public/app-component',
    component: AppComponent
  },
  {
    path:'',
    redirectTo:'public/welcome',
    pathMatch:'full'
  },
  {
    path:'public/welcome',
    component: WelcomeComponent
  }
];
