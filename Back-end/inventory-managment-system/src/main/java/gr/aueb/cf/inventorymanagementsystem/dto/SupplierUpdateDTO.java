package gr.aueb.cf.inventorymanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SupplierUpdateDTO {

    @NotNull(message = "Id is required.")
    private Long id;

    @NotEmpty(message = "Supplier name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotEmpty(message = "Supplier email is required")
    private String email;

    @NotEmpty(message = "Supplier phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    private String phoneNumber;

}
