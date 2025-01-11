import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Category } from 'src/app/shared/interfaces/category';
import { CategoryService } from 'src/app/shared/services/category.service';

declare var bootstrap: any;

@Component({
  selector: 'app-update-category-form',
  templateUrl: './update-category-form.component.html',
  styleUrls: ['./update-category-form.component.css']
})
export class UpdateCategoryFormComponent {

categoryForm: FormGroup;
  categoryService = inject(CategoryService)
  errorMessage = ""
  categoryToUpdate: Category = {
    id: 0,
    name: ""
  }
  updatedCategory: Category = {
    id: 0,
    name: ""
  }

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const categoryId = +params['id'];
      if (categoryId) {
        this.getCategory(categoryId);
      }
    });
  }

  constructor(private fb: FormBuilder, 
    private route: ActivatedRoute,
    private router: Router) {
    // Δημιουργία της φόρμας
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      });
  }

  getCategory(categoryId: number) {
    this.categoryService.getCategory(categoryId).subscribe({
      next: (category) => {
        this.categoryToUpdate = category;
        this.categoryForm.patchValue({
          name: category.name
        });
      },
      error: (err) => {
        console.error('Error fetching category:', err)
        this.errorMessage = "Error fetching category:"
      }
      });
  }

  updateCategory() {
    const categoryPayload: Category = {
      id: this.categoryToUpdate.id, // Χρησιμοποιεί το ID της παραγγελίας που επεξεργάζεστε
      name: this.categoryForm.get('name')?.value || null
    };
      
    this.categoryService.updateCategory(categoryPayload.id, categoryPayload).subscribe({
      next: (response) => {
        console.log("Category updated successfully:", response);
        this.updatedCategory = response;
        console.log("Updated Category:", this.updatedCategory);
        this.errorMessage = ""; // Καθαρισμός προηγούμενων μηνυμάτων σφάλματος
        this.showModal(true); // Εμφάνιση modal για επιτυχία
      },
      error: (error) => {
        this.errorMessage = error?.error.description || "An unexpected error occurred.";
        console.error("Error updating category:", error);
        this.showModal(false); // Εμφάνιση modal για αποτυχία
      }
    });
  }

  // Μέθοδος υποβολής της φόρμας
  onSubmit() {
    if (this.categoryForm.valid) {
      console.log('Category Submitted:', this.categoryForm.value);
      this.errorMessage = "";
      this.updateCategory()
      
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
        this.router.navigate(['/categories']);
      }
    }, 2000);
  }

}
