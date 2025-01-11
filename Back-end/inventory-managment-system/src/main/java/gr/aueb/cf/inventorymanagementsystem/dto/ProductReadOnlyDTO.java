package gr.aueb.cf.inventorymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductReadOnlyDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private SupplierReadOnlyDTO supplier;
    private CategoryReadOnlyDTO category;

}
