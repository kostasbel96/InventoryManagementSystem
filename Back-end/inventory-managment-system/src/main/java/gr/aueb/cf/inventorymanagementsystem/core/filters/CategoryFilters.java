package gr.aueb.cf.inventorymanagementsystem.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CategoryFilters extends GenericFilters{
    @Nullable
    private String name;
}
