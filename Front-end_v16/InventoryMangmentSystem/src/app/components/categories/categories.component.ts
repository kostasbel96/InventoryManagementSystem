import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Category } from 'src/app/shared/interfaces/category';
import { CategoryService } from 'src/app/shared/services/category.service';

declare var bootstrap: any;

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent {
  categoryName: string = ''
  categoryService = inject(CategoryService)
  categories: Category[] = []
  currentPage = 0
  totalPages = 0
  pageSize = 4
  filteredCategories = [...this.categories]
  errorMessage: string = "";
  isError = false

  ngOnInit() {
    this.loadCategories(this.currentPage)
  }
  
  constructor(private router: Router) {
      }

  loadCategories(page: number) {
    this.categoryService.getPaginatedCategories(page, this.pageSize).subscribe({
      next: (response) => {
        this.categories = response.content; // Ενημέρωση της κύριας λίστας προϊόντων
        this.filteredCategories = [...this.categories]; // Ενημέρωση της φιλτραρισμένης λίστας
        this.totalPages = response.totalPages;
        this.currentPage = response.pageable.pageNumber;
        console.log(response)
        console.log('Current Page:', this.currentPage);
        console.log('Total Pages:', this.totalPages);
      },
      error: (error) => {
        console.error('Error fetching categories:', error);
      },
    });
  }

  changePage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      if (this.categoryName && this.categoryName.trim() !== '') {
        // Αν υπάρχει όνομα προϊόντος, κάντε αναζήτηση
        this.getByCategoryName(this.categoryName, this.currentPage);
      } else {
        // Αν όχι, φορτώστε γενικά προϊόντα
        this.loadCategories(page);
      }
    }
  }

  getByCategoryName(name: string, page: number = 0){
    if(name){
      const filters = { name };
      this.currentPage = 0
      this.categoryService.searchCategories(filters, page, this.pageSize).subscribe({
        next: (response) => {
          this.filteredCategories = response.content; // Ενημέρωση φιλτραρισμένης λίστας
          this.totalPages = response.totalPages;
          this.currentPage = response.pageable.pageNumber;
        },
        error: (error) => {
          console.error('Error searching categories:', error);
        },
      });
    }
    else{
      this.currentPage = 0
      this.loadCategories(this.currentPage)
    }
  }

  deleteCategory(id: number) {
    if(confirm("Do you want to delete this category?")){
      this.categoryService.deleteCategory(id).subscribe({
        next: (response) => {
          console.log("ok", response)
          this.loadCategories(0);
          this.showModal(true, "Category deleted!");
        },
        error: (error) =>{
          if (error.status === 409) {
            // 409 Conflict - Integrity Constraint Violation
            this.showModal(false, "Cannot delete category because it is associated with products.");
          } else {
            // Άλλο σφάλμα
            console.error("Unexpected error:", error);
            this.showModal(false, "An unexpected error occurred. Please try again.");
          }
        }
      })
    }
  
}

showModal(isSuccess: boolean, message: string){
  const modalElement = document.getElementById('exampleModal') as HTMLElement;
  const modal = new bootstrap.Modal(modalElement);
  this.errorMessage = message
  modal.show();
  

// Κλείσιμο modal μετά από 2 δευτερόλεπτα
  setTimeout(() => {
    modal.hide();

    if (isSuccess) {
      // Ανακατεύθυνση μόνο αν είναι επιτυχία
      this.router.navigate(['/categories']);
    }
    else{
      this.isError = true
    }
  }, 2000);
}

  
}
