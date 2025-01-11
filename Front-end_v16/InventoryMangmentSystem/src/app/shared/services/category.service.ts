import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../interfaces/category';
import { PaginatedResponse } from '../interfaces/PaginatedResponse';


@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = environment.apiUrl + "/categories";
  
    constructor(private http: HttpClient) { }
  
    saveCategory(category: Category): Observable<Category>{
        return this.http.post<Category>(`${this.apiUrl}/save`, category);
    }
  
    getPaginatedCategories(page: number, size: number): Observable<any> {
      return this.http.get<PaginatedResponse<any>>(`${this.apiUrl}?page=${page}&size=${size}`);
    }
  
    searchCategories(filters: any, page: number, size: number): Observable<any> {
      return this.http.post<any>(`${this.apiUrl}/all?page=${page}&size=${size}`, filters);
    }
  
    getAllCategories(): Observable<Category[]> {
      return this.http.get<Category[]>(`${this.apiUrl}/getAll`)
    }

    deleteCategory(id: number): Observable<Category>{
        return this.http.delete<Category>(`${this.apiUrl}/${id}`);
      }
        
      getCategory(id: number): Observable<Category>{
        return this.http.get<Category>(`${this.apiUrl}/${id}`);
      }
        
      updateCategory(id: number, category: Category): Observable<Category>{
        return this.http.put<Category>(`${this.apiUrl}/${category.id}`, category)
      }
}
