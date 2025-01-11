package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.ValidationException;
import gr.aueb.cf.inventorymanagementsystem.dto.UserInsertDTO;
import gr.aueb.cf.inventorymanagementsystem.dto.UserReadOnlyDTO;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.User;
import gr.aueb.cf.inventorymanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final Mapper mapper;
    private final UserService userService;

    /**
     * Registers a new user.
     *
     * <p>This method handles user registration by validating the input data and saving
     * the user if no errors are found. If validation errors occur or the username is already
     * taken, appropriate exceptions are thrown.</p>
     *
     * @param userInsertDTO the data transfer object containing the user's registration details
     * @param bindingResult the result of input validation
     * @return a {@link ResponseEntity} containing the saved {@link UserReadOnlyDTO} and a status code of 201 (Created)
     * @throws AppObjectAlreadyExists if a user with the given username already exists
     * @throws ValidationException if validation errors are found in the input
     */
    @PostMapping("/users/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user by validating the input and saving the user if valid."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the input data"),
            @ApiResponse(responseCode = "409", description = "User with the given username already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<UserReadOnlyDTO> registerUser(@Valid @RequestBody UserInsertDTO userInsertDTO,
                                                        BindingResult bindingResult)
            throws AppObjectAlreadyExists, ValidationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        UserReadOnlyDTO savedUser = userService.saveUser(userInsertDTO);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

}
