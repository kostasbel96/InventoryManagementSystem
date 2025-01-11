import { HttpClient } from '@angular/common/http';
import { effect, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Credentials, LoggedInUser, User } from '../interfaces/backend-user';
import { environment } from '../../../environments/environment.development';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  http: HttpClient = inject(HttpClient)
  router = inject(Router);
  user = signal<LoggedInUser | null>(null)
  private apiUrl = environment.apiUrl + "/users";
  constructor() {
    const token = localStorage.getItem("access_token");

  if (!token || typeof token !== 'string') {
    console.warn("No valid token found in localStorage.");
    return; // Αν δεν υπάρχει token, σταματήστε εδώ
  }

  try {
    const decodedToken: any = jwtDecode(token);
    console.log('Decoded Token:', decodedToken);

    this.user.set({
      fullname: decodedToken.fullname || 'Unknown',
      username: decodedToken.sub || '',
    });
  } catch (error) {
    console.error('Failed to decode token:', error);
    localStorage.removeItem('access_token'); // Καθαρίστε μη έγκυρα tokens
  }
  }

  registerUser(user: User) {
    return this.http.post<{msg: string}>(`${this.apiUrl}/register`, user)
  }

  loginUser(credentials: Credentials){
    return this.http.post<{
      token: any;access_token: string
    }>(`${environment.apiUrl}/auth/authenticate`, credentials)
  }

  logoutUser(){
        this.user.set(null);
        localStorage.removeItem('access_token');
        this.router.navigate(['welcome']);
    }

}
