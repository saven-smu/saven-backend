package io.bootify.saven.repos;

import io.bootify.saven.domain.Bill;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BillRepository extends JpaRepository<Bill, UUID> {
    List<Bill> findByStoredDateTimeAfter(LocalDateTime storedDateTime);
}
