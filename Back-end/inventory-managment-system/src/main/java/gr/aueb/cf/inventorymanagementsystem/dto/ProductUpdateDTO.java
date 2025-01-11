package gr.aueb.cf.inventorymanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductUpdateDTO {

    @NotNull(message = "Id is required.")
    private Long id;

    @NotNull(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    private Double price;

    @NotNull(message = "Product quantity is required")
    private Integer quantity;

    private SupplierUpdateDTO supplier;

    private CategoryUpdateDTO category;
}
