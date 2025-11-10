import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { StoreLayoutComponent } from './features/store-layout/store-layout.component';
import { DashboardLayoutComponent } from './features/dashboard-layout/dashboard-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { ProfileComponent } from './features/profile/profile.component';
import { CartComponent } from './features/cart/cart.component';
import { UserListComponent } from './features/user-list/user-list.component';
import { ProductListComponent } from './features/product/product-list/product-list.component';
import { OrderComponent } from './features/orders/order/order.component';
import { ProductDetailComponent } from './features/product/product-detail/product-detail.component';
import { ManageProductsComponent } from './features/product/manage-products/manage-products.component';
import { AddEditProductComponent } from './features/product/add-edit-product/add-edit-product.component';
import { ManageOrdersComponent } from './features/orders/manage-orders/manage-orders.component';
import { CategoriesListComponent } from './features/category/categories-list/categories-list.component';

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
    children: [
      { path: '', redirectTo: 'products', pathMatch: 'full' },
      { path: 'profile', component: ProfileComponent },
      { path: 'cart', component: CartComponent },
      { path: 'products', component: ProductListComponent },
      { path: 'products/:id', component: ProductDetailComponent },
      { path: 'orders', component: OrderComponent },
    ],
  },

  //for managers
  {
    path: 'dashboard',
    component: DashboardLayoutComponent,
    children: [
      { path: 'users', component: UserListComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'products', component: ManageProductsComponent },
      { path: 'products/add', component: AddEditProductComponent },
      { path: 'products/edit/:id', component: AddEditProductComponent },
      { path: 'orders', component: ManageOrdersComponent },
      { path: 'categories', component: CategoriesListComponent },
    ],
    canActivate: [authGuard],
    data: { roles: ['ROLE_MANAGER', 'ROLE_ADMIN'] },
  },
];
