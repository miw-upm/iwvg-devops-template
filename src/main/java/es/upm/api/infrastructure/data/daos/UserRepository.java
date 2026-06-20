package es.upm.api.infrastructure.data.daos;

import es.upm.api.infrastructure.data.models.Role;
import es.upm.api.infrastructure.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMobile(String mobile);
    List<User> findByRoleIn(Collection<Role> roles);
}
