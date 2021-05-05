package pl.inteca.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inteca.product.domain.Product;

import java.util.List;

/**
 * Simple DAO for Product
 *
 * @see Product
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCreditIDIn(List<Long> creditIDs);
}
