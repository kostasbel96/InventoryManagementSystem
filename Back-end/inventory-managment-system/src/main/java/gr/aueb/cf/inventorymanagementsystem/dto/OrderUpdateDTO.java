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
public class OrderUpdateDTO {

    @NotNull(message = "Id is required.")
    private Long id;

    @NotNull(message = "Supplier is required.")
    private SupplierUpdateDTO supplier;

    @NotNull(message = "Order Items is required")
    private List<OrderItemUpdateDTO> orderItems;

}
