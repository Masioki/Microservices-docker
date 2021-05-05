package pl.inteca.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.inteca.customer.domain.Customer;

import java.util.Collection;
import java.util.List;

/**
 * Simple DAO for Customer
 *
 * @see Customer
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByPeselIn(List<String> pesels);

    @Query(value = "SELECT DISTINCT customer_pesel FROM credit_id_mapping WHERE credits_ids IN :creditIds ", nativeQuery = true)
    List<String> findPeselsByCreditId(@Param("creditIds") Collection<Long> creditsIds);
}
