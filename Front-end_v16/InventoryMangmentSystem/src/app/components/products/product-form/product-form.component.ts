import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Category } from 'src/app/shared/interfaces/category';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { CategoryService } from 'src/app/shared/services/category.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent {
  productForm: FormGroup;
  productService = inject(ProductService)
  supplierService = inject(SupplierService)
  categoryService = inject(CategoryService)
  errorMessage = ""
  savedProduct: Product = {
    id: 0,
    name: "",
    supplier: null,
    category: null,
    price: 0,
    description: "",
    quantity: 0
  } 
  categories: Category[] = []

  suppliers: Supplier[] = []
  
  ngOnInit(){
    this.loadSuppliers()
    this.loadCategories()
  }  

  constructor(private fb: FormBuilder, private router: Router) {
    // Δημιουργία της φόρμας
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      supplier: [null, [Validators.required]],
      category: [null, [Validators.required]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.maxLength(200)]],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
  }

  loadSuppliers(){
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
      },
      error: (err) => {
        console.error('Error fetching suppliers:', err);
      },
    })
  }

  loadCategories(){
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Error fetching suppliers:', err);
      },
    })
  }

  // Μέθοδος υποβολής της φόρμας
  onSubmit() {
    if (this.productForm.valid) {
      console.log('Product Submitted:', this.productForm.value);
      this.errorMessage = "";
      this.createProduct()
      
    } else {
      console.warn('Form is invalid');
    }

  }

  createProduct(){
    const product = this.productForm.value as Product
    this.productService.saveProduct(product).subscribe({
      next: (response) => {
        console.log("ok")
        this.savedProduct = response;
        this.errorMessage = ""; // Καθαρισμός οποιουδήποτε προηγούμενου λάθους
        this.showModal(true); // Εμφάνιση modal για επιτυχία
        this.productForm.reset(); // Επαναφορά φόρμας
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
        this.router.navigate(['/products']);
      }
    }, 2000);
  }

}
