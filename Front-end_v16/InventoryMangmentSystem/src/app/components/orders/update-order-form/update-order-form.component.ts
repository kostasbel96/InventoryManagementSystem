import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/shared/interfaces/order';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { OrderService } from 'src/app/shared/services/order.service';
import { ProductService } from 'src/app/shared/services/product.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

declare var bootstrap: any;

@Component({
  selector: 'app-update-order-form',
  templateUrl: './update-order-form.component.html',
  styleUrls: ['./update-order-form.component.css']
})
export class UpdateOrderFormComponent {
  orderForm: FormGroup;
  originalProducts: Product[] = [];
  availableProducts: Product[] = [];
  selectedProducts: Product[] = [];
  suppliers: Supplier[] = [];
  errorMessage = ""
  orderToUpdate: Order = {
    id: 0,
    supplier: null,
    orderDate: "",
    orderItems: null
  }
  updatedOrder: Order = {
    id: 0,
    supplier: null,
    orderDate: "",
    orderItems: null
  } 

  ngOnInit() {
    this.loadSuppliers();
    this.loadProducts();
    this.route.params.subscribe((params) => {
      const orderId = +params['id'];
      if (orderId) {
        this.getOrder(orderId);
      }
    });
  }

  constructor(private fb: FormBuilder,
    private route: ActivatedRoute, 
    private supplierService: SupplierService,
    private productService: ProductService,
    private orderService: OrderService, 
    private router: Router) {
    this.orderForm = this.fb.group({
      products: this.fb.array([], Validators.required),
      supplier: [null, [Validators.required]]
    });
  }

  getOrder(id: number){
    this.orderService.getOrder(id).subscribe({
      next: (order) => {
        this.orderToUpdate = order;
        console.log('Supplier in order:', order.supplier);
        this.orderForm.patchValue({
          supplier: order.supplier
        });
        console.log(this.orderForm.get('supplier')?.value)

        this.productsFormArray.clear();
        order.orderItems?.forEach(item => {
          console.log('Adding product to FormArray:', item);
          this.productsFormArray.push(
            this.fb.group({
              product: [item.product, Validators.required],
              quantity: [item.quantity, [Validators.required, Validators.min(1)]]
            })
          );
          this.selectedProducts.push(item.product);
          this.availableProducts = this.availableProducts.filter(
            (product) => product.id !== item.product.id
          );
        });
      },
      error: (err) => console.error('Error fetching order:', err)
    });
  }

  compareSuppliers(s1: Supplier, s2: Supplier): boolean {
    return s1 && s2 ? s1.id === s2.id : s1 === s2;
  }

  compareProducts(p1: Product, p2: Product): boolean {
    return p1 && p2 ? p1.id === p2.id : p1 === p2;
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

  get productsFormArray(): FormArray {
      return this.orderForm.get('products') as FormArray;
  }

  onSubmit(): void {
    if (this.orderForm.valid) {
      console.log('Order submitted:', this.orderForm.value);
      const modalElement = document.getElementById('exampleModal') as HTMLElement;
      const modal = new bootstrap.Modal(modalElement);
      this.updateOrder()
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

  updateOrder() {
    const orderPayload: Order = {
      id: this.orderToUpdate.id, // Χρησιμοποιεί το ID της παραγγελίας που επεξεργάζεστε
      supplier: this.orderForm.get('supplier')?.value || null,
      orderDate: this.orderToUpdate.orderDate, // Διατήρηση της αρχικής ημερομηνίας
      orderItems: this.productsFormArray.value.map((productGroup: any) => ({
        product: productGroup.product,
        quantity: productGroup.quantity
      }))
    };
  
    this.orderService.updateOrder(orderPayload.id, orderPayload).subscribe({
      next: (response) => {
        console.log("Order updated successfully:", response);
        this.updatedOrder = response;
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
