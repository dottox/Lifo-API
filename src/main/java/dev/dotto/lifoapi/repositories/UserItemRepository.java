package dev.dotto.lifoapi.repositories;

import dev.dotto.lifoapi.models.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

}
