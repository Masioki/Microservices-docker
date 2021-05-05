package pl.inteca.credit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inteca.credit.domain.Credit;

/**
 * Simple DAO for Credit entity
 *
 * @see Credit
 */
@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
}
