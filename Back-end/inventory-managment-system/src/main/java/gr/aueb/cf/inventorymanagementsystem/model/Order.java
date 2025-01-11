package gr.aueb.cf.inventorymanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "orders", indexes = {
        @Index(name = "idx_order", columnList = "supplier_id")
})
public class Order extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.PACKAGE)
    private List<OrderItem> orderItems;

    private LocalDate orderDate;

    public List<OrderItem> getAllOrderItems() {
        return orderItems;
    }

    public void addOrderItem(Product product, Integer quantity) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setOrder(this);

        orderItems.add(orderItem);
    }

    public void clearOrderItems() {
        if (this.orderItems != null) {
            // Αποσύνδεση κάθε OrderItem από την παραγγελία
            this.orderItems.forEach(orderItem -> orderItem.setOrder(null));
            // Εκκαθάριση της λίστας
            this.orderItems.clear();
        }
    }


}
