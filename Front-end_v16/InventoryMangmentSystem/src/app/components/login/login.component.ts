import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Credentials, LoggedInUser } from 'src/app/shared/interfaces/backend-user';
import { UserService } from 'src/app/shared/services/user.service';

declare var bootstrap: any;


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form: FormGroup;
  username: string = '';
  invalidLogin = false;

  constructor(private fb: FormBuilder, 
    private router: Router,
    private userService: UserService) {
    // Δημιουργία της φόρμας
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern("^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$")]],
    });
  }
  
    // Μέθοδος υποβολής της φόρμας
    onSubmit() {
      const credentials = {
        username: this.form.get('username')?.value,
        password: this.form.get('password')?.value,
      };
    
      this.userService.loginUser(credentials).subscribe({
        next: (response) => {
          console.log(response)
          const token = response.token;
      
          if (!token || typeof token !== 'string') {
            console.error('Invalid token received:', token);
            this.invalidLogin = true;
            return;
          }

          if (localStorage.getItem("access_token")) {
            localStorage.removeItem("access_token");
          }
          localStorage.setItem("access_token", token);
      
          console.log('Token stored:', token);
          this.router.navigate(['products']);
        },
        error: (error) => {
          console.error('Login failed:', error);
          this.invalidLogin = true;
          this.router.navigate(['login']);
        }
      });
    }
}
