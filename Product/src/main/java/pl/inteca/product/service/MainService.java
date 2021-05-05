package pl.inteca.product.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.inteca.product.domain.Product;
import pl.inteca.product.dto.ProductDTO;
import pl.inteca.product.repository.ProductRepository;

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

    @Autowired
    private ProductRepository repository;

    @Autowired
    private Validator validator;

    /**
     * Validates Product object
     *
     * @param product object to validate
     * @throws ValidationException if not valid
     */
    private void assertProductValid(Product product) throws ValidationException {
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        Optional<ConstraintViolation<Product>> opt = violations.stream().findFirst();
        if (opt.isPresent())
            throw new ValidationException(opt.get().getMessage());
    }

    /**
     * @param ids credit ids
     * @return product with specified credit ids
     */
    public List<ProductDTO> getProductsByCreditId(List<Long> ids) {
        return repository
                .findByCreditIDIn(ids)
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Creates new product
     *
     * @param productDTO new product data
     * @return new product ID
     * @throws ValidationException wrong data format
     */
    public long createProduct(ProductDTO productDTO) throws ValidationException {
        log.info("Create new product: " + productDTO);
        Product product = Product.newFromDTO(productDTO);

        // is product data in valid format
        try {
            assertProductValid(product);
        } catch (Exception e) {
            log.info("Product invalid: " + product);
            throw e;
        }

        // save validated product
        repository.save(product);
        log.info("New product saved: " + product);

        return product.getID();
    }

    /**
     * Removes product
     *
     * @param productID ID of product to remove
     */
    public void removeById(long productID) {
        if (repository.existsById(productID)) {
            repository.deleteById(productID);
            log.info("Product: " + productID + " deleted");
        }
    }
}
