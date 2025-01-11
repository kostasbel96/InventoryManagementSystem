package gr.aueb.cf.inventorymanagementsystem.dto;

import gr.aueb.cf.inventorymanagementsystem.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReadOnlyDTO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private Role role;
}