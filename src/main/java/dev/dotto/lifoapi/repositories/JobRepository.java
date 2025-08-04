package dev.dotto.lifoapi.repositories;

import dev.dotto.lifoapi.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
