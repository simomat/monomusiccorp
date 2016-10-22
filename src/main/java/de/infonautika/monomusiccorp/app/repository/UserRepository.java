package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String userName);
}
