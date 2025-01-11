package gr.aueb.cf.inventorymanagementsystem.rest;

import gr.aueb.cf.inventorymanagementsystem.authentication.AuthenticationService;
import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.inventorymanagementsystem.dto.AuthenticationRequestDTO;
import gr.aueb.cf.inventorymanagementsystem.dto.AuthenticationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user by username and password and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed",
                    content = @Content
            )
    })
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO)
            throws AppObjectNotAuthorizedException {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }
}
