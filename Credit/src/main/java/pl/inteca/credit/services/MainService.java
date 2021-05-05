package pl.inteca.credit.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.inteca.credit.domain.Credit;
import pl.inteca.credit.dto.CreditDTO;
import pl.inteca.credit.dto.CreditInfoDTO;
import pl.inteca.credit.dto.CustomerDTO;
import pl.inteca.credit.dto.ProductDTO;
import pl.inteca.credit.exception.CreditException;
import pl.inteca.credit.repositories.CreditRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main application service providing logic for MainCreditController
 *
 * @see pl.inteca.credit.controllers.MainCreditController
 */
@Service
@Slf4j
public class MainService {
    // Credit DAO
    @Autowired
    private CreditRepository creditRepository;

    // Product service client
    @Autowired
    private ProductService productService;

    // Customer service client
    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    /**
     * Asserts if object valid according to its fields annotations
     *
     * @param credit object to validate
     * @throws CreditException object not valid
     */
    private void assertCreditValid(Credit credit) throws ValidationException {
        Set<ConstraintViolation<Credit>> violations = validator.validate(credit);
        Optional<ConstraintViolation<Credit>> opt = violations.stream().findFirst();
        if (opt.isPresent())
            throw new ValidationException(opt.get().getMessage());
    }

    /**
     * @return all available credits data
     * @throws Exception internal error, probably caused by connection with other microservice
     */
    public List<CreditInfoDTO> getAllCredits() throws Exception {
        // all data from own database
        List<Credit> credits = creditRepository.findAll();
        List<Long> creditIDs = credits.stream().map(Credit::getID).collect(Collectors.toList());

        // matching data from customer and product service
        List<CustomerDTO> customerDTOS;
        List<ProductDTO> productDTOS;
        try {
            customerDTOS = customerService.getCustomers(creditIDs);
            productDTOS = productService.getProducts(creditIDs);
        } catch (Exception e) {
            log.error("Get all request for other services rejected: " + e.getMessage());
            throw e;
        }

        // assembling all collected data
        return credits.stream()
                .map(CreditDTO::new)
                .map(credit ->
                        new CreditInfoDTO(
                                credit,
                                customerDTOS.stream().filter(cus -> cus.getCreditID() == credit.getID()).findFirst().orElse(null),
                                productDTOS.stream().filter(p -> p.getCreditID() == credit.getID()).findFirst().orElse(null)
                        )
                ).collect(Collectors.toList());
    }

    /**
     * Creates and save new credit
     *
     * @param creditInfo full info
     * @return new credit ID
     * @throws Exception       internal error
     * @throws CreditException data not valid
     */
    @Transactional
    public long createCredit(CreditInfoDTO creditInfo) throws ValidationException, Exception {
        log.info("Create new credit: " + creditInfo);
        // create new entity
        Credit credit = Credit.newFromDTO(creditInfo.getCredit());

        // validate data format of credit
        // other entities will be validated with appropriate microservice
        assertCreditValid(credit);


        // persist to assign ID, but dont save to DB yet
        entityManager.persist(credit);
        long creditID = credit.getID();
        log.info("Credit data valid, ID assigned: " + creditID);

        creditInfo.getProduct().setCreditID(creditID);
        creditInfo.getCustomer().setCreditID(creditID);

        // create product
        try {
            long productID = productService.createProduct(creditInfo.getProduct());
            try {
                // create customer
                customerService.createCustomer(creditInfo.getCustomer());
            } catch (Exception e) {
                // in case of error remove product data created above
                productService.remove(productID);
                throw e;
            }
        } catch (Exception e) {
            log.info("Credit creation rejected by other services, ID=" + creditID);
        }

        // save properly created credit, with already assigned ID, to DB
        creditRepository.save(credit);
        log.info("Credit created, ID=" + creditID);
        return creditID;
    }
}
