package dev.forge.unifit.user;


import dev.forge.unifit.exception.UserAlreadyExistException;

import java.util.List;

public interface IUserService {
    List<User> getAllUsers();
    User registerUser(UserDTO userDTO) throws UserAlreadyExistException;
    User findByEmail(String email);
    boolean emailExists(String email);
    User getUser(Long id);

    User saveUser(User user);

    User updateUser(User user);

}

