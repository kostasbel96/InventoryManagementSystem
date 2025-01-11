import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-suppliers',
  templateUrl: './suppliers.component.html',
  styleUrls: ['./suppliers.component.css']
})
export class SuppliersComponent {
  supplierName: string = '';
  supplierService = inject(SupplierService)
  isError = false
  suppliers: Supplier[] = []
  currentPage = 0
  totalPages = 0
  pageSize = 4
  filteredSuppliers = [...this.suppliers]
  errorMessage: string = "";

  ngOnInit() {
    this.loadSuppliers(this.currentPage)
  }

  constructor(private router: Router) {
    }

  loadSuppliers(page: number) {
    this.supplierService.getPaginatedSuppliers(page, this.pageSize).subscribe({
      next: (response) => {
        this.suppliers = response.content; // Ενημέρωση της κύριας λίστας προϊόντων
        this.filteredSuppliers = [...this.suppliers]; // Ενημέρωση της φιλτραρισμένης λίστας
        this.totalPages = response.totalPages;
        this.currentPage = response.pageable.pageNumber;
        console.log(response)
        console.log('Current Page:', this.currentPage);
        console.log('Total Pages:', this.totalPages);
      },
      error: (error) => {
        console.error('Error fetching suppliers:', error);
      },
    });
  }

  deleteSupplier(id: number) {
      if(confirm("Do you want to delete this supplier?")){
        this.supplierService.deleteSupplier(id).subscribe({
          next: (response) => {
            console.log("ok", response)
            this.loadSuppliers(0);
            this.showModal(true, "Supplier deleted!");
          },
          error: (error) =>{
            if (error.status === 409) {
              // 409 Conflict - Integrity Constraint Violation
              this.showModal(false, "Cannot delete supplier because it is associated with products.");
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
        this.router.navigate(['/suppliers']);
      }
      else{
        this.isError = true
      }
    }, 2000);
  }

  changePage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      if (this.supplierName && this.supplierName.trim() !== '') {
        // Αν υπάρχει όνομα προϊόντος, κάντε αναζήτηση
        this.getBySupplierName(this.supplierName, this.currentPage);
      } else {
        // Αν όχι, φορτώστε γενικά προϊόντα
        this.loadSuppliers(page);
      }
    }
  }

  getBySupplierName(name: string, page: number = 0) {
    if(name){
      const filters = { name };
      this.currentPage = 0
      this.supplierService.searchSuppliers(filters, page, this.pageSize).subscribe({
        next: (response) => {
          this.filteredSuppliers = response.content; // Ενημέρωση φιλτραρισμένης λίστας
          this.totalPages = response.totalPages;
          this.currentPage = response.pageable.pageNumber;
        },
        error: (error) => {
          console.error('Error searching suppliers:', error);
        },
      });
    }
    else{
      this.currentPage = 0
      this.loadSuppliers(this.currentPage)
    }
  }

}
