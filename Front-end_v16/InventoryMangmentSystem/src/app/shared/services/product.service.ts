import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../interfaces/product';
import { PaginatedResponse } from '../interfaces/PaginatedResponse';


@Injectable({
  providedIn: 'root'
})
export class ProductService {
  
  private apiUrl = environment.apiUrl + "/products";

  constructor(private http: HttpClient) { }

  saveProduct(product: Product): Observable<Product>{
    return this.http.post<Product>(`${this.apiUrl}/save`, product);
  }

  getPaginatedProducts(page: number, size: number): Observable<any> {
    return this.http.get<PaginatedResponse<any>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  searchProducts(filters: any, page: number, size: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/all?page=${page}&size=${size}`, filters);
  }

  getAllProducts() : Observable<Product[]> {
      return this.http.get<Product[]>(`${this.apiUrl}/getAll`)
  }

  deleteProduct(id: number): Observable<Product>{
      return this.http.delete<Product>(`${this.apiUrl}/${id}`);
    }
  
    getProduct(id: number): Observable<Product>{
      return this.http.get<Product>(`${this.apiUrl}/${id}`);
    }
  
    updateProduct(id: number, product: Product): Observable<Product>{
      return this.http.put<Product>(`${this.apiUrl}/${product.id}`, product)
    }

}
