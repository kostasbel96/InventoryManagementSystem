import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../services/user.service';
import { jwtDecode } from 'jwt-decode';

export const authGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

  const token = localStorage.getItem('access_token');
  if (!token) {
    return router.navigate(['login']);
  }

  try {
    const decodedToken: any = jwtDecode(token);
    const isExpired = decodedToken.exp * 1000 < Date.now();

    if (isExpired) {
      console.warn('Token has expired.');
      localStorage.removeItem('access_token');
      return router.navigate(['login']);
    }

    const userRole = decodedToken.role;
    if (route.data['roles'] && !route.data['roles'].includes(userRole)) {
      console.warn('User does not have permission for this route.');
      return router.navigate(['welcome']);
    }

    return true;
  } catch (error) {
    console.error('Invalid token:', error);
    localStorage.removeItem('access_token');
    return router.navigate(['login']);
  }
};
