package gr.aueb.cf.inventorymanagementsystem.dto;


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
public class OrderReadOnlyDTO {

    private Long id;
    private SupplierReadOnlyDTO supplier;
    private LocalDate orderDate;
    private List<OrderItemReadOnlyDTO> orderItems;

}
