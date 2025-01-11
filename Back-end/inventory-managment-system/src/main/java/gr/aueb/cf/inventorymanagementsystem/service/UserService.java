package gr.aueb.cf.inventorymanagementsystem.service;

import gr.aueb.cf.inventorymanagementsystem.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.inventorymanagementsystem.dto.UserInsertDTO;
import gr.aueb.cf.inventorymanagementsystem.dto.UserReadOnlyDTO;
import gr.aueb.cf.inventorymanagementsystem.mapper.Mapper;
import gr.aueb.cf.inventorymanagementsystem.model.User;
import gr.aueb.cf.inventorymanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    /**
     * Saves a new user to the database.
     *
     * <p>This method saves a user based on the provided {@link UserInsertDTO}. If a user
     * with the same username already exists, an {@link AppObjectAlreadyExists} exception is thrown.</p>
     *
     * @param userInsertDTO the data transfer object containing the user's information
     * @return a {@link UserReadOnlyDTO} representing the saved user
     * @throws AppObjectAlreadyExists if a user with the given username already exists
     */
    @Transactional
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws AppObjectAlreadyExists {

        if(userRepository.findByUsername(userInsertDTO.getUsername()).isPresent())
            throw new AppObjectAlreadyExists("User",
                    "User with username: " +
                     userInsertDTO.getUsername() + " already exists.");
        User user = mapper.mapToUserEntity(userInsertDTO);
        return mapper.mapToUserReadOnlyDTO(userRepository.save(user));
    }
}