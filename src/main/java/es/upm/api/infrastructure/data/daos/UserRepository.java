package es.upm.api.infrastructure.data.daos;

import es.upm.api.infrastructure.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMobile(String mobile);
}
