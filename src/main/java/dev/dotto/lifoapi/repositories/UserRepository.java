package dev.dotto.lifoapi.repositories;

import dev.dotto.lifoapi.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long userId);
    boolean existsByUsername(@NotBlank @Size(min = 3, max = 30) String username);
    boolean existsByEmail(@NotBlank @Email @Size(max = 50) String email);
}
