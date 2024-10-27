package dev.forge.unifit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.email <> :adminEmail")
    List<String> findAllEmailsExceptAdmin(@Param("adminEmail") String adminEmail);
}
