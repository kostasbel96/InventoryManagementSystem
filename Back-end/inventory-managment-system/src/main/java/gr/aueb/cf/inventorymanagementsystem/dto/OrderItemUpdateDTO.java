package gr.aueb.cf.inventorymanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemUpdateDTO {

    @NotNull(message = "Id is required.")
    private Long id;

    @NotEmpty(message = "Product is required")
    private ProductUpdateDTO product;

    @Min(value = 1, message = "Quantity must be 1 or greater")
    @NotEmpty(message = "Quantity is required")
    private Integer quantity;

}
