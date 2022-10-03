package io.bootify.saven.repos;

import io.bootify.saven.domain.Bill;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BillRepository extends JpaRepository<Bill, UUID> {
}
