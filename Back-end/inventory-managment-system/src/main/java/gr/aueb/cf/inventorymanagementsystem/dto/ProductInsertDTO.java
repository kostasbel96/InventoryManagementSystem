package gr.aueb.cf.inventorymanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductInsertDTO {


    @NotNull(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    private Double price;

    @NotNull(message = "Product quantity is required")
    private Integer quantity;

    private SupplierInsertDTO supplier;

    private CategoryInsertDTO category;

}
