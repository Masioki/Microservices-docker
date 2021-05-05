package pl.inteca.credit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.inteca.credit.dto.CreditInfoDTO;
import pl.inteca.credit.exception.CreditException;
import pl.inteca.credit.services.MainService;

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
public class MainCreditController {
    @Autowired
    private MainService service;

    /**
     * @return all stored credits
     */
    @GetMapping("/get/all")
    public ResponseEntity<List<CreditInfoDTO>> getAllCredits(HttpServletRequest request) {
        try {
            log.info("Get all credits request from: " + request.getRemoteAddr());
            return ResponseEntity.ok(service.getAllCredits());
        } catch (Exception e) {
            // unexpected exception, so 5xx is returned
            log.error("Get all: Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Creates new Credit
     *
     * @param creditInfoDTO credit data
     * @return new credit ID
     */
    @PostMapping("/create")
    public ResponseEntity<String> createCredit(@Valid @RequestBody CreditInfoDTO creditInfoDTO, HttpServletRequest request) {
        try {
            log.info("New credit creation attempt by: " + request.getRemoteAddr() + " data: " + creditInfoDTO);
            long id = service.createCredit(creditInfoDTO);
            return ResponseEntity.ok(Long.toString(id));
        } catch (CreditException | ValidationException ce) {
            // messages from CreditException and ValidationException are safe to pass to user
            return ResponseEntity.badRequest().body(ce.getMessage());
        } catch (Exception e) {
            // other messages should be hidden for security reasons
            log.error("Create credit: Internal error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try later");
        }
    }
}
