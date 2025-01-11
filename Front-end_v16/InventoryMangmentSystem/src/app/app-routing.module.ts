import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './components/products/products.component';
import { OrdersComponent } from './components/orders/orders.component';
import { SuppliersComponent } from './components/suppliers/suppliers.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { LoginComponent } from './components/login/login.component';
import { ProductFormComponent } from './components/products/product-form/product-form.component';
import { OrderFormComponent } from './components/orders/order-form/order-form.component';
import { SupplierFormComponent } from './components/suppliers/supplier-form/supplier-form.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { CategoryFormComponent } from './components/categories/category-form/category-form.component';
import { RegisterFormComponent } from './components/register-form/register-form.component';
import { UpdateOrderFormComponent } from './components/orders/update-order-form/update-order-form.component';
import { UpdateProductFormComponent } from './components/products/update-product-form/update-product-form.component';
import { UpdateSupplierFormComponent } from './components/suppliers/update-supplier-form/update-supplier-form.component';
import { UpdateCategoryFormComponent } from './components/categories/update-category-form/update-category-form.component';
import { authGuard } from './shared/guards/auth.guard';

const routes: Routes = [
  { path: 'products', component: ProductsComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'USER'] } },
  { path: 'orders', component: OrdersComponent, canActivate: [authGuard] , data: { roles: ['ADMIN', 'USER'] }},
  { path: 'suppliers', component: SuppliersComponent, canActivate: [authGuard] , data: { roles: ['ADMIN'] }},
  { path: 'categories', component: CategoriesComponent, canActivate: [authGuard] , data: { roles: ['ADMIN'] }},
  { path: 'welcome', component: WelcomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'insertProduct', component: ProductFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'USER'] } },
  { path: 'insertOrder', component: OrderFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'USER'] } },
  { path: 'insertSupplier', component: SupplierFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'insertCategory', component: CategoryFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'register', component: RegisterFormComponent },
  { path: 'orders/edit/:id', component: UpdateOrderFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'USER'] } },
  { path: 'products/edit/:id', component: UpdateProductFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN', 'USER'] } },
  { path: 'suppliers/edit/:id', component: UpdateSupplierFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'categories/edit/:id', component: UpdateCategoryFormComponent, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: '', redirectTo: 'welcome', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
