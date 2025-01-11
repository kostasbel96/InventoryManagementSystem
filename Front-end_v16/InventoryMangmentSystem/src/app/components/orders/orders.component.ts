import { Component, inject } from '@angular/core';
import { Order } from 'src/app/shared/interfaces/order';
import { OrderItem } from 'src/app/shared/interfaces/order-item';
import { Product } from 'src/app/shared/interfaces/product';
import { Supplier } from 'src/app/shared/interfaces/supplier';
import { OrderService } from 'src/app/shared/services/order.service';
import { SupplierService } from 'src/app/shared/services/supplier.service';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent {

  orders: Order[] = []
  supplierName: string = '';
  filteredOrders = [...this.orders]
  orderService = inject(OrderService)
  currentPage = 0
  totalPages = 0
  pageSize = 4

  ngOnInit(){
    this.loadOrders(this.currentPage)
  }

  loadOrders(page: number){
    this.orderService.getPaginatedOrders(page, this.pageSize).subscribe({
      next: (response) => {
        this.orders = response.content; // Ενημέρωση της κύριας λίστας προϊόντων
        this.filteredOrders = [...this.orders]; // Ενημέρωση της φιλτραρισμένης λίστας
        this.totalPages = response.totalPages;
        this.currentPage = response.pageable.pageNumber;
        console.log('Current Page:', this.currentPage);
        console.log('Total Pages:', this.totalPages);
      },
      error: (error) => {
        this.orders = [];
        this.filteredOrders = [];
        console.error('Error fetching products:', error);
      },
    });
  }

  deleteOrder(id: number) {
    if(confirm("Do you want to delete this order?")){
      this.orderService.deleteOrder(id).subscribe({
        next: (response) => {
          console.log("ok", response)
          this.loadOrders(0);
        },
        error: (err) =>{
          console.log(err)
        }
      })
    }
    
  }

  getTotalAmount(id: number){
    const order = this.filteredOrders.find(o => o.id === id);
    if (!order || !order.orderItems || !Array.isArray(order.orderItems)) {
      console.warn('Invalid order or order items:', order);
      return 0;
    }

    return order.orderItems.reduce((total, orderItem) => {
      if (!orderItem.product || typeof orderItem.product.price !== 'number') {
        console.warn('Invalid product in order item:', orderItem);
        return total;
      }

      return total + (orderItem.product.price * orderItem.quantity);
    }, 0);
  }

  getBySupplierName(name: string, page: number = 0) {
    if(name){
      const filters = { name };
      this.currentPage = 0
      this.orderService.searchOrders(filters, page, this.pageSize).subscribe({
        next: (response) => {
          console.log('Filtered Orders:', response);
          this.filteredOrders = response.content; // Ενημέρωση φιλτραρισμένης λίστας
          this.totalPages = response.totalPages;
          this.currentPage = response.pageable.pageNumber;
        },
        error: (error) => {
          console.error('Error searching orders:', error);
        },
      });
    }
    else{
      this.currentPage = 0
      this.loadOrders(this.currentPage)
    }
  }

  changePage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      if (this.supplierName && this.supplierName.trim() !== '') {
        // Αν υπάρχει όνομα προϊόντος, κάντε αναζήτηση
        this.getBySupplierName(this.supplierName, this.currentPage);
      } else {
        // Αν όχι, φορτώστε γενικά προϊόντα
        this.loadOrders(page);
      }
    }
  }

}
