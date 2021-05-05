package pl.inteca.product.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inteca.product.dto.ProductDTO;
import pl.inteca.product.service.MainService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;

/**
 * Main API controller
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MainProductController {

    @Autowired
    private MainService service;

    /**
     * @param creditIDs credits
     * @return all products for specified credits
     */
    @PostMapping("/get/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts(@RequestBody List<Long> creditIDs, HttpServletRequest request) {
        log.info("Get all products request from: " + request.getRemoteAddr());
        return service.getProductsByCreditId(creditIDs);
    }

    /**
     * Creates new product
     *
     * @param productDTO new product data
     * @return product ID or message in case of bad request
     */
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductDTO productDTO, HttpServletRequest request) {
        try {
            log.info("Create product request from: " + request.getRemoteAddr() + ", data: " + productDTO);
            long id = service.createProduct(productDTO);
            return ResponseEntity.ok(Long.toString(id));
        } catch (ValidationException ve) {
            // messages from ValidationException are safe to pass to user
            return ResponseEntity.badRequest().body(ve.getMessage());
        } catch (Exception e) {
            // other messages should be hidden for security reasons
            log.error("Create product: Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try later");
        }
    }

    /**
     * Removes product
     *
     * @param productID ID of product to remove
     * @return status message
     */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeProductById(@PathVariable("id") long productID, HttpServletRequest request) {
        try {
            log.info("Remove product request from: " + request.getRemoteAddr() + ", productID: " + productID);
            service.removeById(productID);
            return ResponseEntity.ok("Product removed!");
        } catch (Exception e) {
            log.error("Remove product: Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
