package pl.inteca.customer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inteca.customer.dto.CustomerDTO;
import pl.inteca.customer.service.MainService;

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
public class MainCustomerController {

    @Autowired
    private MainService service;

    /**
     * Get all customers for specified credits
     *
     * @param creditIDs credits IDs for which to find customers
     * @return list of customers
     */
    @PostMapping("/get/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getByCreditID(@RequestBody List<Long> creditIDs, HttpServletRequest request) {
        log.info("Get all by credit id request from: " + request.getRemoteAddr());
        return service.getCustomersByCreditId(creditIDs);
    }

    /**
     * Adds new credit for customer. Will create new customer if specified didnt exist
     *
     * @param customerDTO customer data
     * @return status message
     */
    @PostMapping("/add")
    public ResponseEntity<String> addCreditForCustomer(@Valid @RequestBody CustomerDTO customerDTO, HttpServletRequest request) {
        try {
            log.info("Add credit for customer request from: " + request.getRemoteAddr() + " data: " + customerDTO);
            service.createCustomer(customerDTO);
            return ResponseEntity.ok("Customer created!");
        } catch (ValidationException ve) {
            return ResponseEntity.badRequest().body(ve.getMessage());
        }
    }
}
