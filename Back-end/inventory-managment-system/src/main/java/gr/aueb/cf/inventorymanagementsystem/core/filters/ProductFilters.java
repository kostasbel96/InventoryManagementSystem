package gr.aueb.cf.inventorymanagementsystem.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductFilters extends GenericFilters {

    @Nullable
    private String name;

    @Nullable
    private String supplierName;

    @Nullable
    private String categoryName;



}
