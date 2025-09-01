import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { LoginComponent } from './features/login/login.component';
import { StoreLayoutComponent } from './features/store-layout/store-layout.component';
import { DashboardLayoutComponent } from './features/dashboard-layout/dashboard-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { ProfileComponent } from './features/profile/profile.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    // canActivate: [AuthGuardService]
    // children:
  },

  //for guests and users
  {
    path: '',
    component: StoreLayoutComponent,
    children: [{ path: 'profile', component: ProfileComponent }],
  },

  //for managers
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    children: [
      //   { path: '', component: DashboardHomeComponent },
      //   { path: 'products', component: ManageProductsComponent },
      //   { path: 'orders', component: ManageOrdersComponent },
      //   { path: 'users', component: ManageUsersComponent }, // admin only
      { path: 'profile', component: ProfileComponent },
    ],
    canActivate: [authGuard],
    data: { roles: ['ROLE_MANAGER'] }, // <-- this is route.data['roles']
  },
];
