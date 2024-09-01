package dev.forge.unifit.authentication.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);

    @Override
    void delete(Role role);
}
