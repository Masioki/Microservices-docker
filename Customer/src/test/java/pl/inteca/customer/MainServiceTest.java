package pl.inteca.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.customer.domain.Customer;
import pl.inteca.customer.dto.CustomerDTO;
import pl.inteca.customer.repository.CustomerRepository;
import pl.inteca.customer.service.MainService;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
public class MainServiceTest {
    @Autowired
    private MainService service;

    @MockBean
    private CustomerRepository repository;

    /**
     * Given invalid customer throw error
     * Given proper customer dont throw and save
     */
    @Test
    public void createCustomerTest() {
        CustomerDTO c = new CustomerDTO();
        c.setSurname("");
        c.setFirstName("f");
        c.setPesel("12345678901");
        c.setCreditID(1);

        // blank surname
        String mes = assertThrows(ValidationException.class, () -> service.createCustomer(c)).getMessage();
        assertEquals(mes, "Surname cannot be empty");

        c.setSurname("s");
        c.setPesel("1234567");

        // wrong pesel format
        mes = assertThrows(ValidationException.class, () -> service.createCustomer(c)).getMessage();
        assertEquals(mes, "Pesel format is 11 numerical characters");

        // ok
        c.setPesel("12345678901");
        assertDoesNotThrow(() -> service.createCustomer(c));

        // was saved to database?
        verify(repository, times(1)).save(any());
    }

    @Test
    public void getCustomersByCreditIdTest() {
        // sample customers
        List<Customer> customers = new ArrayList<>();
        List<CustomerDTO> dtos = new ArrayList<>();

        CustomerDTO c = new CustomerDTO();
        c.setPesel("12345678902");
        c.setCreditID(2);
        c.setSurname("s");
        c.setFirstName("f");
        dtos.add(c);
        Customer customer = Customer.newFromDTO(c);
        customer.addCredit(2);
        customers.add(customer);

        // two credits for same customer
        c = new CustomerDTO();
        c.setPesel("12345678903");
        c.setCreditID(3);
        c.setSurname("s");
        c.setFirstName("f");
        dtos.add(c);
        customer = Customer.newFromDTO(c);
        customer.addCredit(3);
        customers.add(customer);

        c = new CustomerDTO();
        c.setPesel("12345678903");
        c.setCreditID(4);
        c.setSurname("s");
        c.setFirstName("f");
        customer.addCredit(4);

        // find all customers, but look only for credits 2 and 3
        List<Long> ids = List.of(2L, 3L);
        List<String> pesels = List.of("12345678903", "12345678902");
        given(repository.findPeselsByCreditId(ids)).willReturn(pesels);
        given(repository.findByPeselIn(pesels)).willReturn(customers);

        // was unnecessary credit filtered?
        assertEquals(service.getCustomersByCreditId(ids), dtos);
    }
}
