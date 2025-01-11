import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-update-supplier-form',
  templateUrl: './update-supplier-form.component.html',
  styleUrls: ['./update-supplier-form.component.css']
})
export class UpdateSupplierFormComponent {
  supplierForm: FormGroup;
  supplierService = inject(SupplierService)
  errorMessage = ""
  supplierToUpdate: Supplier = {
    id: 0,
    name: "",
    email: "",
    phoneNumber: ""
  }
  updatedSupplier: Supplier = {
    id: 0,
    name: "",
    email: "",
    phoneNumber: ""
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const supplierId = +params['id'];
      if (supplierId) {
        this.getSupplier(supplierId);
      }
    });
  }

  constructor(private fb: FormBuilder, 
    private route: ActivatedRoute,
    private router: Router) {
    // Δημιουργία της φόρμας
    this.supplierForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.maxLength(10), Validators.minLength(10)]],
    });
  }

  getSupplier(supplierId: number) {
    this.supplierService.getSupplier(supplierId).subscribe({
      next: (supplier) => {
        this.supplierToUpdate = supplier;
        this.supplierForm.patchValue({
          name: supplier.name,
          email: supplier.email,
          phoneNumber: supplier.phoneNumber
        });
      },
      error: (err) => {
        console.error('Error fetching product:', err)
        this.errorMessage = "Error fetching product:"
      }
      });
  }

  updateSupplier() {
    const supplierPayload: Supplier = {
      id: this.supplierToUpdate.id, // Χρησιμοποιεί το ID της παραγγελίας που επεξεργάζεστε
      name: this.supplierForm.get('name')?.value || null,
      email: this.supplierForm.get('email')?.value || null,
      phoneNumber: this.supplierForm.get('phoneNumber')?.value
    };
      
    this.supplierService.updateSupplier(supplierPayload.id, supplierPayload).subscribe({
      next: (response) => {
        console.log("Supplier updated successfully:", response);
        this.updatedSupplier = response;
        console.log("Updated Supplier:", this.updatedSupplier);
        this.errorMessage = ""; // Καθαρισμός προηγούμενων μηνυμάτων σφάλματος
        this.showModal(true); // Εμφάνιση modal για επιτυχία
      },
      error: (error) => {
        this.errorMessage = error?.error.description || "An unexpected error occurred.";
        console.error("Error updating supplier:", error);
        this.showModal(false); // Εμφάνιση modal για αποτυχία
      }
    });
  }

  // Μέθοδος υποβολής της φόρμας
  onSubmit() {
    if (this.supplierForm.valid) {
      console.log('Supplier Submitted:', this.supplierForm.value);
      this.errorMessage = "";
      this.updateSupplier()
      
    } else {
      console.warn('Form is invalid');
    }
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
