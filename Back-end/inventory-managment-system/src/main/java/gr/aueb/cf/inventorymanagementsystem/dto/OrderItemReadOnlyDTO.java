package gr.aueb.cf.inventorymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemReadOnlyDTO {

    private Long id;
    private ProductReadOnlyDTO product;
    private Integer quantity;
}
