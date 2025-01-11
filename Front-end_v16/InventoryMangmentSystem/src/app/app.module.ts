import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ListGroupMenuComponent } from './components/list-group-menu/list-group-menu.component';
import { RouterLink, RouterLinkActive} from '@angular/router';
import { ProductsComponent } from './components/products/products.component';
import { OrdersComponent } from './components/orders/orders.component';
import { SuppliersComponent } from './components/suppliers/suppliers.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { LoginComponent } from './components/login/login.component';
import { FormsModule } from '@angular/forms';
import { ProductFormComponent } from './components/products/product-form/product-form.component';
import { ReactiveFormsModule } from '@angular/forms'; 
import { Router } from '@angular/router';
import { OrderFormComponent } from './components/orders/order-form/order-form.component';
import { SupplierFormComponent } from './components/suppliers/supplier-form/supplier-form.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { CategoryFormComponent } from './components/categories/category-form/category-form.component';
import { RegisterFormComponent } from './components/register-form/register-form.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { UpdateOrderFormComponent } from './components/orders/update-order-form/update-order-form.component';
import { UpdateProductFormComponent } from './components/products/update-product-form/update-product-form.component';
import { UpdateSupplierFormComponent } from './components/suppliers/update-supplier-form/update-supplier-form.component';
import { UpdateCategoryFormComponent } from './components/categories/update-category-form/update-category-form.component';
import { AuthInterceptor } from './shared/services/auth.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    ListGroupMenuComponent,
    ProductsComponent,
    OrdersComponent,
    SuppliersComponent,
    WelcomeComponent,
    LoginComponent,
    ProductFormComponent,
    OrderFormComponent,
    SupplierFormComponent,
    CategoriesComponent,
    CategoryFormComponent,
    RegisterFormComponent,
    UpdateOrderFormComponent,
    UpdateProductFormComponent,
    UpdateSupplierFormComponent,
    UpdateCategoryFormComponent

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterLink,
    RouterLinkActive,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true 
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
