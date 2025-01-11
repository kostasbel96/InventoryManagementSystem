import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { ProductService } from 'src/app/shared/services/product.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent {
  productName: string = '';
  productService = inject(ProductService)
  supplierService = inject(SupplierService)
  suppliers: Supplier[] = []

  products: Product[] = []
  filteredProducts: Product[] = []
  form: FormGroup;
  currentPage = 0
  totalPages = 0
  pageSize = 4

  ngOnInit() {
    this.loadProducts(this.currentPage)
    this.loadSuppliers();
  }
  

  constructor(private fb: FormBuilder, private router: Router) {
    // Δημιουργία της φόρμας
    this.form = this.fb.group({
      quantity: [0, Validators.min(0)]
    });
  }

  loadProducts(page: number) {
    this.productService.getPaginatedProducts(page, this.pageSize).subscribe({
      next: (response) => {
        this.products = response.content; // Ενημέρωση της κύριας λίστας προϊόντων
        this.filteredProducts = [...this.products]; // Ενημέρωση της φιλτραρισμένης λίστας
        this.totalPages = response.totalPages;
        this.currentPage = response.pageable.pageNumber;
        console.log(response)
        console.log('Current Page:', this.currentPage);
        console.log('Total Pages:', this.totalPages);
      },
      error: (error) => {
        console.error('Error fetching products:', error);
      },
    });
  }

  loadSuppliers() {
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
      },
      error: (err) => {
        console.error('Error fetching suppliers:', err);
      },
    })
  }

  changePage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      if (this.productName && this.productName.trim() !== '') {
        // Αν υπάρχει όνομα προϊόντος, κάντε αναζήτηση
        this.getByProductName(this.productName, this.currentPage);
      } else {
        // Αν όχι, φορτώστε γενικά προϊόντα
        this.loadProducts(page);
      }
    }
  }

  getSupplierNameOfProduct(id: number | undefined){
    if(!id) return ""
    let supplier = this.suppliers.filter((supplier) => supplier.id === id);
    if (supplier.length === 0) {
        console.warn(`No supplier found with ID: ${id}`);
        return null;
    }
    return supplier[0].name;
  }

  deleteProduct(id: number) {
    if(confirm("Do you want to delete this product?")){
      this.productService.deleteProduct(id).subscribe({
        next: (response) => {
          console.log("ok", response)
          this.loadProducts(0);
        },
        error: (err) =>{
          console.log(err)
        }
      })
    }
    
  }

  getByProductName(name: string, page: number = 0) {
    if(name){
      const filters = { name };
      this.currentPage = 0
      this.productService.searchProducts(filters, page, this.pageSize).subscribe({
        next: (response) => {
          console.log('Filtered Products:', response);
          this.filteredProducts = response.content; // Ενημέρωση φιλτραρισμένης λίστας
          this.totalPages = response.totalPages;
          this.currentPage = response.pageable.pageNumber;
        },
        error: (error) => {
          console.error('Error searching products:', error);
        },
      });
    }
    else{
      this.currentPage = 0
      this.loadProducts(this.currentPage)
    }
  }

}
