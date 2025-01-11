import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { UserService } from './shared/services/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'InventoryMangmentSystem';
  userService: UserService = inject(UserService);

  constructor(private router: Router) {}

  isLoggedIn(): boolean {
    const token = localStorage.getItem('access_token');
    if (!token) {
      return false;
    }

    try {
      const decodedToken: any = jwtDecode(token); 
      return decodedToken.exp * 1000 > Date.now(); 
    } catch (error) {
      console.error('Invalid token:', error);
      return false;
    }
  }

  logout(): void {
    this.userService.logoutUser();
  }
}
