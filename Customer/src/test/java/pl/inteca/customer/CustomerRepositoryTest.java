package pl.inteca.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.customer.domain.Customer;
import pl.inteca.customer.dto.CustomerDTO;
import pl.inteca.customer.repository.CustomerRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository repository;


    /**
     * Test custom query
     */
    @Test
    public void findPeselsByCreditIdTest() {
        // populate database with sample customers
        CustomerDTO c = new CustomerDTO();
        c.setSurname("s");
        c.setFirstName("f");

        // two credits for same customer
        c.setPesel("12345678901");
        Customer customer = Customer.newFromDTO(c);
        customer.addCredit(1);
        customer.addCredit(2);
        entityManager.persist(customer);

        c.setPesel("12345678902");
        customer = Customer.newFromDTO(c);
        customer.addCredit(3);
        entityManager.persist(customer);

        c.setPesel("12345678903");
        customer = Customer.newFromDTO(c);
        customer.addCredit(4);
        entityManager.persist(customer);
        entityManager.flush();

        // should return pesels of second and thirs customer
        List<Long> creditIDs = List.of(3L, 4L);
        List<String> found = repository.findPeselsByCreditId(creditIDs);
        assertEquals(found.size(), 2);
        assertThat(found).isEqualTo(List.of("12345678902", "12345678903"));

        // should return just 2 pesels, because first customer has two credits - 1 and 2
        creditIDs = List.of(1L, 2L, 4L);
        found = repository.findPeselsByCreditId(creditIDs);
        assertEquals(found.size(), 2);
        assertThat(found).isEqualTo(List.of("12345678901", "12345678903"));
    }

}
