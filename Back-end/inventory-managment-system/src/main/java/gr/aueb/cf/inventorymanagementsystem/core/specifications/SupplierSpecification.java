package gr.aueb.cf.inventorymanagementsystem.core.specifications;

import gr.aueb.cf.inventorymanagementsystem.model.Supplier;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification {

    private SupplierSpecification() {

    }

    public static Specification<Supplier> supplierNameIs(String name) {
        return trStringFieldLike("name", name);
    }





    public static Specification<Supplier> trStringFieldLike(String field, String value) {
        return (root, query, builder) -> {
            if (value == null || value.trim().isEmpty()) return builder.isTrue(builder.literal(true));
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        };
    }
}
