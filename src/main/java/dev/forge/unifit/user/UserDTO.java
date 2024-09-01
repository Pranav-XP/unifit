package dev.forge.unifit.user;

import dev.forge.unifit.validation.PasswordMatches;
import dev.forge.unifit.validation.ValidEmail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
@PasswordMatches
public class UserDTO {
    @NotNull
    @Size(min = 1, message = "{Size.userDto.firstName}")
    private String firstName;

    @NotNull
    @Size(min = 1, message = "{Size.userDto.lastName}")
    private String lastName;

    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1, message = "{Size.userDto.email}")
    private String email;


    public void setEmail(final String email) {
        this.email = email;
    }


    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setMatchingPassword(final String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    @Override
    public String toString() {
        return "UserDto [firstName=" +
                firstName +
                ", lastName=" +
                lastName +
                ", email=" +
                email +
                ", role=" + "]";
    }
}
