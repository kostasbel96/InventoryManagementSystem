package gr.aueb.cf.inventorymanagementsystem.core.specifications;

import gr.aueb.cf.inventorymanagementsystem.model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    private CategorySpecification() {

    }

    public static Specification<Category> categoryNameIs(String name) {
        return trStringFieldLike("name", name);
    }





    public static Specification<Category> trStringFieldLike(String field, String value) {
        return (root, query, builder) -> {
            if (value == null || value.trim().isEmpty()) return builder.isTrue(builder.literal(true));
            return builder.like(builder.upper(root.get(field)), value.toUpperCase() + "%");
        };
    }

}
