package gr.aueb.cf.inventorymanagementsystem.authentication;

import gr.aueb.cf.inventorymanagementsystem.dto.AuthenticationRequestDTO;
import gr.aueb.cf.inventorymanagementsystem.dto.AuthenticationResponseDTO;
import gr.aueb.cf.inventorymanagementsystem.model.User;
import gr.aueb.cf.inventorymanagementsystem.repository.UserRepository;
import gr.aueb.cf.inventorymanagementsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {
            // throws AppObjectNotAuthorizedException {
        // Authenticate and Create an authentication token from username and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        // If authentication was successful, generate JWT token
        String token = jwtService.generateToken(authentication.getName(), user.getRole().name());
        return new AuthenticationResponseDTO(user.getFirstname(), user.getLastname(), token);
    }
}
