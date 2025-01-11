package gr.aueb.cf.inventorymanagementsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderInsertDTO {

    @NotNull(message = "Supplier is required")
    private SupplierInsertDTO supplier;

    @NotNull(message = "Order Items is required")
    private List<OrderItemInsertDTO> orderItems;

}
