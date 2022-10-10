package io.bootify.saven.repos;

import io.bootify.saven.domain.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findDistinctByEmail(String email);
}
