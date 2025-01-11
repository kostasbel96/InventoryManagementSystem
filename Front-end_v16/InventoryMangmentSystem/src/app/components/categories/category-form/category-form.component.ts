import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router'
import { Category } from 'src/app/shared/interfaces/category';
import { CategoryService } from 'src/app/shared/services/category.service';

declare var bootstrap: any;

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent {
  categoryForm: FormGroup;
  categoryService = inject(CategoryService)
  errorMessage = ""
  savedCategory: Category = {
      id: 0,
      name: ""
    }

  constructor(private fb: FormBuilder, private router: Router) {
    // Δημιουργία της φόρμας
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  // Μέθοδος υποβολής της φόρμας
  onSubmit() {
    if (this.categoryForm.valid) {
      console.log('Category Submitted:', this.categoryForm.value);
      this.errorMessage = "";
      this.createCategory()
      
    } else {
      console.warn('Form is invalid');
    }
  }  
  createCategory() {
    const category = this.categoryForm.value as Category
    this.categoryService.saveCategory(category).subscribe({
      next: (response) => {
        console.log("ok")
        this.savedCategory = response;
        this.errorMessage = ""; // Καθαρισμός οποιουδήποτε προηγούμενου λάθους
        this.showModal(true); // Εμφάνιση modal για επιτυχία
        this.categoryForm.reset(); // Επαναφορά φόρμας
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
        this.router.navigate(['/categories']);
      }
    }, 2000);
  }

}  

