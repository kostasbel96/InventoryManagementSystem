package gr.aueb.cf.inventorymanagementsystem.core.specifications;

import gr.aueb.cf.inventorymanagementsystem.model.Category;
import gr.aueb.cf.inventorymanagementsystem.model.Product;
import gr.aueb.cf.inventorymanagementsystem.model.Supplier;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class
 */
public class ProductSpecification {

    private ProductSpecification() {

    }

    public static Specification<Product> productNameIs(String name) {
        return trStringFieldLike("name", name);
    }

    public static Specification<Product> trSupplierNameIs(String name) {
        return (root, query, builder) -> {
            if (name == null || name.isBlank()) {
                return builder.isTrue(builder.literal(true));
            }

            Join<Product, Supplier> supplier = root.join("supplier");

            return builder.equal(supplier.get("name"), name);
        };
    }

    public static Specification<Product> trCategoryNameIs(String name) {
        return (root, query, builder) -> {
            // Αν το όνομα είναι null ή κενό, επιστρέφουμε συνθήκη που είναι πάντα αληθής
            if (name == null || name.isBlank()) {
                return builder.isTrue(builder.literal(true));
            }

            // Join με την οντότητα Category που σχετίζεται με την οντότητα Product
            Join<Product, Category> category = root.join("category");

            // Επιστροφή συνθήκης όπου το όνομα της κατηγορίας ταιριάζει με το δοθέν όνομα
            return builder.equal(category.get("name"), name);
        };
    }

    public static Specification<Product> trStringFieldLike(String field, String value) {
        return (root, query, builder) -> {
            if (value == null || value.trim().isEmpty()) return builder.isTrue(builder.literal(true));
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        };
    }
}
