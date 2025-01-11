import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../interfaces/order';
import { PaginatedResponse } from '../interfaces/PaginatedResponse';
@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.apiUrl + "/orders"

  constructor(private http: HttpClient) { }

  saveOrder(order: Order): Observable<Order>{
    return this.http.post<Order>(`${this.apiUrl}/save`, order)
  }

  getPaginatedOrders(page: number, size: number): Observable<any> {
    return this.http.get<PaginatedResponse<any>>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  searchOrders(filters: any, page: number, size: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/all?page=${page}&size=${size}`, filters);
  }

  deleteOrder(id: number): Observable<Order>{
    return this.http.delete<Order>(`${this.apiUrl}/${id}`);
  }

  getOrder(id: number): Observable<Order>{
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  updateOrder(id: number, order: Order): Observable<Order>{
    return this.http.put<Order>(`${this.apiUrl}/${order.id}`, order)
  }

}
