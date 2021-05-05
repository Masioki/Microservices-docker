package pl.inteca.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.inteca.customer.controller.MainCustomerController;
import pl.inteca.customer.domain.Customer;
import pl.inteca.customer.dto.CustomerDTO;
import pl.inteca.customer.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Whole application test
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerApplicationTests {

    @Autowired
    private MainCustomerController controller;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private List<CustomerDTO> customers;

    @BeforeAll
    public void populateDB() {
        // populate database with sample customers

        customers = new ArrayList<>();
        CustomerDTO c = new CustomerDTO();
        c.setSurname("s");
        c.setFirstName("f");
        c.setPesel("12345678901");
        c.setCreditID(1);
        customers.add(c);
        Customer customer = Customer.newFromDTO(c);
        customer.addCredit(1);
        repository.save(customer);

        c = new CustomerDTO();
        c.setPesel("12345678902");
        c.setCreditID(2);
        c.setSurname("s");
        c.setFirstName("f");
        customers.add(c);
        customer = Customer.newFromDTO(c);
        customer.addCredit(2);
        repository.save(customer);

        // two credits for same customer
        c = new CustomerDTO();
        c.setPesel("12345678903");
        c.setCreditID(3);
        c.setSurname("s");
        c.setFirstName("f");
        customers.add(c);
        customer = Customer.newFromDTO(c);
        customer.addCredit(3);

        c = new CustomerDTO();
        c.setPesel("12345678903");
        c.setCreditID(4);
        c.setSurname("s");
        c.setFirstName("f");
        customers.add(c);
        customer.addCredit(4);
        repository.save(customer);
    }

    @Test
    @Order(0)
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    @Order(1)
    public void getAllCustomersTest() throws Exception {
        List<Long> testIDs = List.of(1L, 3L, 4L);
        List<CustomerDTO> properResult = List.of(customers.get(0), customers.get(2), customers.get(3));

        // are proper customers returned?
        mockMvc.perform(
                post("/api/get/all")
                        .content(new ObjectMapper().writeValueAsString(testIDs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(properResult)));
    }


    @Test
    @Order(2)
    public void createCustomerTest() throws Exception {
        CustomerDTO c = new CustomerDTO();
        c.setSurname("");
        c.setFirstName("f");
        c.setPesel("12345678910");
        c.setCreditID(10);

        // blank surname, should return 4xx
        mockMvc.perform(
                post("/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(c)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        c.setSurname("s");
        c.setPesel("123ss");

        // wrong pesel format, should return 4xx
        mockMvc.perform(
                post("/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(c)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        c.setPesel("12345678910");

        // proper customer format, should be created
        mockMvc.perform(
                post("/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(c)))
                .andDo(print())
                .andExpect(status().isOk());

        // was new product saved in database?
        assertFalse(repository.findById("12345678910").isEmpty());
    }

}
