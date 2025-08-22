package dev.dotto.lifoapi.repositories;

import dev.dotto.lifoapi.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.findLevel <= ?1 AND i.findLevel > 0")
    List<Item> findItemsByFindLevelLessThanEqual(int level);
}
