package gr.aueb.cf.inventorymanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name")
})
public class Product extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.PACKAGE)
    private List<OrderItem> orderItems;

    public List<OrderItem> getAllOrderItems() {
       return orderItems != null ? Collections.unmodifiableList(orderItems) : Collections.emptyList();
    }

    public void addOrderItem(Order order, Integer quantity) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setQuantity(quantity);
        orderItem.setProduct(this);

        orderItems.add(orderItem);
    }

    public void clearOrderItems() {
        if (this.orderItems != null) {
            // Αποσύνδεση κάθε OrderItem από την παραγγελία
            this.orderItems.forEach(orderItem -> orderItem.setProduct(null));
            // Εκκαθάριση της λίστας
            this.orderItems.clear();
        }
    }

}
