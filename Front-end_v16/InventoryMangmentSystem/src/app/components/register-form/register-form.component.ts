import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/shared/interfaces/backend-user';
import { UserService } from 'src/app/shared/services/user.service';

declare var bootstrap: any;


@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css']
})
export class RegisterFormComponent {
  registerForm: FormGroup;
  insertedUser: string = '';
  userService = inject(UserService)
  registrationStatus: { success: boolean, message: string } = {
    success:false,
    message: "Not attempted yet"
}

    constructor(private fb: FormBuilder, private router: Router) {
      // Δημιουργία της φόρμας
      this.registerForm = this.fb.group({
        fname: ['', [Validators.required]],
        lname: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]],
        role: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.pattern("^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$")]],
        confirmPassword: ['', [Validators.required, Validators.pattern("^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$")]],
      }, { validator: this.passwordMatchValidator });
    }

    passwordMatchValidator(form: FormGroup) {
      return form.get('password')?.value === form.get('confirmPassword')?.value
            ? null: { mismatch: true };
    }

    get f() { return this.registerForm.controls; }
  
    // Μέθοδος υποβολής της φόρμας
    onSubmit() {
      if (this.registerForm.valid) {
        this.insertedUser = this.registerForm.get('fname')?.value + " " +
              this.registerForm.get('lname')?.value
        const user: User = {
                firstname: this.registerForm.get('fname')?.value || '',
                lastname: this.registerForm.get('lname')?.value || '',
                username: this.registerForm.get('email')?.value || '',
                role: this.registerForm.get('role')?.value,
                password: this.registerForm.get('password')?.value || ''
            }
        this.userService.registerUser(user).subscribe({
          next: (response) =>{
            console.log("No errors")
            this.registrationStatus = {success: true, message: response.msg}
          },
          error: (response) => {
            console.log("Errors",response)
            let message = response.error.msg;
            this.registrationStatus = {success: false, message: message}
          }
        })
        
        const modalElement = document.getElementById('exampleModal') as HTMLElement;
        const modal = new bootstrap.Modal(modalElement);
        modal.show();
        // Καθαρισμός της φόρμας μετά την υποβολή
        this.registerForm.reset({
          fname: '',
          lname: '',
          email: '',
          password: '',
          confirmPassword: ''
        });
        
        setTimeout(() => {
          modal.hide(); // Κλείσιμο του modal
          this.router.navigate(['/login']);
        }, 2000);
      } else {
        console.warn('Form is invalid');
      }
    
    }
}
