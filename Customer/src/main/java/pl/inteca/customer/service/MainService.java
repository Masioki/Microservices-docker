package pl.inteca.customer.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pl.inteca.customer.domain.Customer;
import pl.inteca.customer.dto.CustomerDTO;
import pl.inteca.customer.repository.CustomerRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main application service
 */
@Service
@Slf4j
public class MainService {

    // Customer DAO
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private Validator validator;

    /**
     * Validates Customer object
     *
     * @param customer object to validate
     * @throws ValidationException if object not valid
     */
    private void assertCustomerValid(Customer customer) throws ValidationException {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Optional<ConstraintViolation<Customer>> opt = violations.stream().findFirst();
        if (opt.isPresent())
            throw new ValidationException(opt.get().getMessage());
    }

    /**
     * Maps Customer object to List of CustomerDTO filtered by credit IDs
     *
     * @param customer  object to map
     * @param creditIDs credit IDs
     * @return List of Customer DTO created from Customer
     */
    private List<CustomerDTO> mapToCustomerDTO(@NonNull Customer customer, @NonNull List<Long> creditIDs) {
        return customer.getCreditsIds()
                .stream()
                .filter(creditIDs::contains)
                .map(id -> new CustomerDTO(customer.getPesel(), customer.getFirstName(), customer.getSurname(), id))
                .collect(Collectors.toList());
    }

    /**
     * @param pesel Customer ID
     * @return Customer object if found, null otherwise
     */
    @Nullable
    public Customer getCustomer(String pesel) {
        return repository.findById(pesel).orElse(null);
    }

    /**
     * @param ids credits IDs
     * @return List of customers who has credits with IDs in specified list
     */
    public List<CustomerDTO> getCustomersByCreditId(@NonNull List<Long> ids) {
        List<String> pesels = repository.findPeselsByCreditId(ids);
        List<Customer> customers = repository.findByPeselIn(pesels);

        return customers
                .stream()
                .flatMap(c -> mapToCustomerDTO(c, ids).stream())
                .collect(Collectors.toList());
    }

    /**
     * Creates new customer
     *
     * @param customerDTO dto object from which to create new Customer
     * @throws ValidationException wrong data format
     */
    public void createCustomer(@NonNull CustomerDTO customerDTO) throws ValidationException {
        log.info("Create customer / add credit for customer attempt: " + customerDTO);

        // if customer does not exists create new one
        Customer customer = getCustomer(customerDTO.getPesel());
        if (customer == null) {
            customer = Customer.newFromDTO(customerDTO);
            log.info("Customer does not exists, attempt to create new one, Pesel=" + customer.getPesel());
        }

        // is customer data valid
        try {
            assertCustomerValid(customer);
        } catch (Exception e) {
            log.info("Customer data invalid, Pesel=" + customer.getPesel());
            throw e;
        }
        customer.addCredit(customerDTO.getCreditID());

        repository.save(customer);
        log.info("Customer with new credit saved, Pesel=" + customer.getPesel() + ", creditID=" + customerDTO.getCreditID());
    }
}
