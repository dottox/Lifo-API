package dev.dotto.lifoapi.repositories;

import dev.dotto.lifoapi.models.GameRole;
import dev.dotto.lifoapi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(GameRole gameRole);
}
