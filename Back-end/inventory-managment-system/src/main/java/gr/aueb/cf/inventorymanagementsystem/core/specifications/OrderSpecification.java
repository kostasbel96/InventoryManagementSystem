package gr.aueb.cf.inventorymanagementsystem.core.specifications;

import gr.aueb.cf.inventorymanagementsystem.model.Order;
import gr.aueb.cf.inventorymanagementsystem.model.Product;
import gr.aueb.cf.inventorymanagementsystem.model.Supplier;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    private OrderSpecification() {

    }

    public static Specification<Order> trSupplierNameIs(String name) {
        return (root, query, builder) -> {
            if (name == null || name.isBlank()) {
                return builder.conjunction();
            }

            // Δημιουργία join με τον πίνακα suppliers
            Join<Order, Supplier> supplierJoin = root.join("supplier");

            // Προσθήκη του φίλτρου για το όνομα
            return builder.like(
                    builder.lower(supplierJoin.get("name")),
                     name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Order> trStringFieldLike(String field, String value) {
        return (root, query, builder) -> {
            if (value == null || value.trim().isEmpty()) return builder.isTrue(builder.literal(true));
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        };
    }

}
