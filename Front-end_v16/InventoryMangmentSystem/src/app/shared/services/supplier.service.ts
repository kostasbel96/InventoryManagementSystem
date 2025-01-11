import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { Supplier } from '../interfaces/supplier';
import { PaginatedResponse } from '../interfaces/PaginatedResponse';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private apiUrl = environment.apiUrl + "/suppliers";

  constructor(private http: HttpClient) { }

  saveSupplier(supplier: Supplier): Observable<Supplier>{
      return this.http.post<Supplier>(`${this.apiUrl}/save`, supplier);
  }

  getPaginatedSuppliers(page: number, size: number): Observable<any> {
    return this.http.get<PaginatedResponse<any>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  searchSuppliers(filters: any, page: number, size: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/all?page=${page}&size=${size}`, filters);
  }

  getAllSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(`${this.apiUrl}/getAll`)
  }

  deleteSupplier(id: number): Observable<Supplier>{
    return this.http.delete<Supplier>(`${this.apiUrl}/${id}`);
  }
    
  getSupplier(id: number): Observable<Supplier>{
    return this.http.get<Supplier>(`${this.apiUrl}/${id}`);
  }
    
  updateSupplier(id: number, supplier: Supplier): Observable<Supplier>{
    return this.http.put<Supplier>(`${this.apiUrl}/${supplier.id}`, supplier)
  }

}
