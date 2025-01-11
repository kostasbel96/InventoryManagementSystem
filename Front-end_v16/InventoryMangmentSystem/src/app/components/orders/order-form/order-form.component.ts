import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Order } from 'src/app/shared/interfaces/order';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;
@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent {
  orderForm: FormGroup;
  originalProducts: Product[] = [];
  availableProducts: Product[] = [];
  selectedProducts: Product[] = [];
  suppliers: Supplier[] = [];
  errorMessage = ""
  savedOrder: Order = {
    id: 0,
    supplier: null,
    orderDate: "",
    orderItems: null
  } 

  

  constructor(private fb: FormBuilder, 
    private supplierService: SupplierService,
    private productService: ProductService,
    private orderService: OrderService, 
    private router: Router) {
    this.orderForm = this.fb.group({
      products: this.fb.array([], Validators.required),
      supplier: ['', [Validators.required]]
    });
  }
  
  ngOnInit() {
    this.loadSuppliers();
    this.addProduct(); // Προσθήκη πρώτου προϊόντος κατά την εκκίνηση
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.originalProducts = data;
        this.availableProducts = [...this.originalProducts]
        console.log(this.availableProducts)
      },
      error: (err) => {
        console.error('Error fetching products:', err);
      }
    });
  }

  loadSuppliers() {
    this.supplierService.getAllSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
      },
      error: (err) => {
        console.error('Error fetching suppliers:', err);
      }
    });
  }

  get productsFormArray(): FormArray {
    return this.orderForm.get('products') as FormArray;
  }

  addProduct(): void {
    const productGroup = this.fb.group({
      product: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });

    productGroup.get('product')?.valueChanges.subscribe((selectedProduct: Product | null) => {
      if (selectedProduct) {
        // Αφαίρεση του επιλεγμένου προϊόντος από τη λίστα
        if (!this.selectedProducts.some(p => p.id === selectedProduct.id)) {
          this.selectedProducts.push(selectedProduct);
          console.log('Selected Products:', this.selectedProducts);
        }
      }
    });

    this.productsFormArray.push(productGroup);
  }

  removeProduct(index: number): void {
    const removedProduct = this.productsFormArray.at(index).get('product')?.value;
    if (removedProduct) {
      // Προσθήκη του αφαιρεθέντος προϊόντος πίσω στη λίστα
      this.selectedProducts = this.selectedProducts.filter(
        (product) => product.id !== removedProduct.id
      );
      console.log(this.selectedProducts)
    }
    this.productsFormArray.removeAt(index);
  }

  isProductSelected(product: Product): boolean {
    return this.selectedProducts.some((selected) =>
      selected.id === product.id);
  }

  

  // Handle form submission
  onSubmit(): void {
    if (this.orderForm.valid) {
      console.log('Order submitted:', this.orderForm.value);
      const modalElement = document.getElementById('exampleModal') as HTMLElement;
      const modal = new bootstrap.Modal(modalElement);
      this.createOrder()
      modal.show();
      this.orderForm.reset();
      this.productsFormArray.clear();
      this.addProduct(); // Add an empty product row after reset
      setTimeout(() => {
          modal.hide(); // Κλείσιμο του modal
          this.router.navigate(['/orders']);
        }, 2000);
    } else {
      console.warn('Form is invalid');
    }
  }
  createOrder() {
    const orderPayload: Order = {
      id: 0, // Αν είναι νέο order, χρησιμοποιήστε 0 ή null
      supplier: this.orderForm.get('supplier')?.value || null,
      orderDate: new Date().toISOString(), // Χρησιμοποιήστε την τρέχουσα ημερομηνία
      orderItems: this.productsFormArray.value.map((productGroup: any) => ({
        product: productGroup.product,
        quantity: productGroup.quantity
      }))
    };
    // const order = this.orderForm.value as Order
    this.orderService.saveOrder(orderPayload).subscribe({
      next: (response) => {
        console.log("ok")
        this.savedOrder = response;
        this.errorMessage = ""; // Καθαρισμός οποιουδήποτε προηγούμενου λάθους
        this.showModal(true); // Εμφάνιση modal για επιτυχία
        this.resetForm(); // Επαναφορά φόρμας
      },
      error: (error) => {
        this.errorMessage = error?.error.description || "An unexpected error occurred.";
        console.error("There was a problem: ", error);
        this.showModal(false); // Εμφάνιση modal για αποτυχία
      }
    })
  }

  resetForm(): void {
    this.orderForm.reset();
    this.productsFormArray.clear();
    this.selectedProducts = [];
    this.addProduct(); // Προσθέτει ένα κενό προϊόν
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
        this.router.navigate(['/orders']);
      }
    }, 2000);
  }
  
}
