package gr.aueb.cf.inventorymanagementsystem.dto;


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
public class CategoryUpdateDTO {

    @NotNull(message = "Id is required.")
    private Long Id;

    @NotEmpty(message = "Category name is required")
    private String name;
}
