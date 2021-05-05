package pl.inteca.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.product.domain.Product;
import pl.inteca.product.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository repository;


    @Test
    public void findByCreditIDInTest() {
        // populate db
        List<Product> temp = List.of(
                new Product(1, "Name one", 1),
                new Product(2, "Name two", 2),
                new Product(3, "Name three", 3),
                new Product(4, "Name four", 4),
                new Product(5, "Name five", 5)
        );
        temp.forEach(entityManager::persist);
        entityManager.flush();

        // credit IDs
        List<Long> ids = List.of(1L, 2L, 3L);

        List<Product> found = repository.findByCreditIDIn(ids);

        // was correct products found ?
        assertEquals(found.size(), 3);
        assertThat(found.stream().map(Product::getCreditID).collect(Collectors.toList()))
                .isEqualTo(ids);
    }
}
