package gr.aueb.cf.inventorymanagementsystem.dto;

import gr.aueb.cf.inventorymanagementsystem.core.enums.Role;
import jakarta.persistence.Entity;
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
public class UserInsertDTO {

    @Email(message = "Please enter a valid email.")
    private String username;

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@$!%*?&]).{5,}$",
            message = "Invalid password")
    private String password;

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String lastname;

    @NotNull(message = "Role is required.")
    private Role role;


}
