package gr.aueb.cf.inventorymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupplierReadOnlyDTO {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
