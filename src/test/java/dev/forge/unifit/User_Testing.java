package dev.forge.unifit.user;

import dev.forge.unifit.authentication.role.Role;
import dev.forge.unifit.authentication.role.RoleRepository;
import dev.forge.unifit.exception.UserAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class User_Testing {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private UserDTO userDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDTO();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singletonList(roleRepository.findByName("ROLE_USER")));
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

   @Test
void testRegisterUser_Success() throws UserAlreadyExistException {
    // Arrange
    when(userRepository.findByEmail(userDto.getEmail())).thenReturn(null); // No existing user
    when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword"); // Mock password encoding
    when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role("ROLE_USER")); // Mock role retrieval

    // Mock the save operation to return a User object
    when(userRepository.save(any(User.class))).thenReturn(user); // Ensure save returns the user object

    // Act
    User registeredUser = userService.registerUser(userDto);

    // Assert
    assertNotNull(registeredUser); // Ensure the registered user is not null
    assertEquals(userDto.getEmail(), registeredUser.getEmail()); // Check if the email matches
    verify(userRepository, times(1)).save(any(User.class)); // Verify that save was called once
}


    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(user);

        // Act & Assert
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerUser(userDto);
        });

        assertEquals("There is an account with that email address: john.doe@example.com", exception.getMessage());
    }

    @Test
    void testFindByEmail() {
        // Arrange
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(user);

        // Act
        User foundUser = userService.findByEmail(userDto.getEmail());

        // Assert
        assertNotNull(foundUser);
        assertEquals(userDto.getEmail(), foundUser.getEmail());
    }

    @Test
    void testEmailExists_True() {
        // Arrange
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(user);

        // Act
        boolean exists = userService.emailExists(userDto.getEmail());

        // Assert
        assertTrue(exists);
    }

    @Test
    void testEmailExists_False() {
        // Arrange
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(null);

        // Act
        boolean exists = userService.emailExists(userDto.getEmail());

        // Assert
        assertFalse(exists);
    }

    @Test
    void testGetUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUser(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        user.setFirstName("Jane");
        user.setLastName("Smith");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User updatedUser = userService.updateUser(user);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
