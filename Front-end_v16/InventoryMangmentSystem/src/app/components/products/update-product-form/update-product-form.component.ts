import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Category } from 'src/app/shared/interfaces/category';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { CategoryService } from 'src/app/shared/services/category.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-update-product-form',
  templateUrl: './update-product-form.component.html',
  styleUrls: ['./update-product-form.component.css']
})
export class UpdateProductFormComponent {
  productForm: FormGroup;
  productToUpdate: Product = {
    id: 0,
    name: "",
    description: "",
    price: 0,
    quantity: 0,
    supplier: null,
    category: null
  }
  updatedProduct: Product = {
    id: 0,
    name: "",
    description: "",
    price: 0,
    quantity: 0,
    supplier: null,
    category: null
  }
  categories: Category[] = []
  suppliers: Supplier[] = []
  errorMessage = ""

  ngOnInit() {
    this.loadSuppliers()
    this.loadCategories()
    this.route.params.subscribe((params) => {
      const productId = +params['id'];
      if (productId) {
        this.getProduct(productId);
      }
    });
  }
  

constructor(private route: ActivatedRoute,
    private supplierService: SupplierService,
    private productService: ProductService,
    private categoryService: CategoryService,
    private fb: FormBuilder,
    private router: Router) {
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

  getProduct(productId: number) {
    this.productService.getProduct(productId).subscribe({
      next: (product) => {
        this.productToUpdate = product;
        this.productForm.patchValue({
          supplier: product.supplier,
          category: product.category,
          name: this.productToUpdate.name,
          description: this.productToUpdate.description,
          price: this.productToUpdate.price,
          quantity: this.productToUpdate.quantity
        });
      },
      error: (err) => {
        console.error('Error fetching product:', err)
        this.errorMessage = "Error fetching product:"
      }
      });
  }

  compareSuppliers(s1: Supplier, s2: Supplier): boolean {
    return s1 && s2 ? s1.id === s2.id : s1 === s2;
  }

  compareCategories(c1: Category, c2: Category): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  updateProduct() {
      const productPayload: Product = {
        id: this.productToUpdate.id, // Χρησιμοποιεί το ID της παραγγελίας που επεξεργάζεστε
        supplier: this.productForm.get('supplier')?.value || null,
        category: this.productForm.get('category')?.value || null,
        name: this.productForm.get('name')?.value,
        description: this.productForm.get('description')?.value,
        price: this.productForm.get('price')?.value,
        quantity: this.productForm.get('quantity')?.value
      };
    
      this.productService.updateProduct(productPayload.id, productPayload).subscribe({
        next: (response) => {
          console.log("Order updated successfully:", response);
          this.updatedProduct = response;
          console.log("Updated Product:", this.updatedProduct);
          this.errorMessage = ""; // Καθαρισμός προηγούμενων μηνυμάτων σφάλματος
          this.showModal(true); // Εμφάνιση modal για επιτυχία
        },
        error: (error) => {
          this.errorMessage = error?.error.description || "An unexpected error occurred.";
          console.error("Error updating order:", error);
          this.showModal(false); // Εμφάνιση modal για αποτυχία
        }
      });
    }

    onSubmit(): void {
      if (this.productForm.valid) {
        console.log('Product submitted:', this.productForm.value);
        const modalElement = document.getElementById('exampleModal') as HTMLElement;
        const modal = new bootstrap.Modal(modalElement);
        this.updateProduct()
        modal.show();
        this.productForm.reset();
        setTimeout(() => {
            modal.hide(); // Κλείσιμο του modal
            this.router.navigate(['/products']);
          }, 2000);
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
          this.router.navigate(['/products']);
        }
      }, 2000);
    }

}
