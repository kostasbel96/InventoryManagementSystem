import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { SupplierService } from 'src/app/shared/services/supplier.service';


declare var bootstrap: any;


@Component({
  selector: 'app-supplier-form',
  templateUrl: './supplier-form.component.html',
  styleUrls: ['./supplier-form.component.css']
})
export class SupplierFormComponent {
  supplierForm: FormGroup;
  supplierService = inject(SupplierService)
  errorMessage = ""
  savedSupplier: Supplier = {
    id: 0,
    name: "",
    email: "",
    phoneNumber: ""
  }

  constructor(private fb: FormBuilder, private router: Router) {
    // Δημιουργία της φόρμας
    this.supplierForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(10), Validators.minLength(10)]],
    });
  }

    // Μέθοδος υποβολής της φόρμας
  onSubmit() {
    if (this.supplierForm.valid) {
      console.log('Supplier Submitted:', this.supplierForm.value);
      this.errorMessage = "";
      this.createSupplier()
      
    } else {
      console.warn('Form is invalid');
    }
  }

  createSupplier(){
      const supplier = this.supplierForm.value as Supplier
      this.supplierService.saveSupplier(supplier).subscribe({
        next: (response) => {
          console.log("ok")
          this.savedSupplier = response;
          this.errorMessage = ""; // Καθαρισμός οποιουδήποτε προηγούμενου λάθους
          this.showModal(true); // Εμφάνιση modal για επιτυχία
          this.supplierForm.reset(); // Επαναφορά φόρμας
        },
        error: (error) => {
          this.errorMessage = error?.error.description || "An unexpected error occurred.";
          console.error("There was a problem: ", error);
          this.showModal(false); // Εμφάνιση modal για αποτυχία
        }
      })
    }
  
    showModal(isSuccess: boolean){
      const modalElement = document.getElementById('exampleModal') as HTMLElement;
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
  
    // Κλείσιμο modal μετά από 2 δευτερόλεπτα
      setTimeout(() => {
        modal.hide();
  
        if (isSuccess) {
          // Ανακατεύθυνση μόνο αν είναι επιτυχία
          this.router.navigate(['/suppliers']);
        }
      }, 2000);
    }
  
}
