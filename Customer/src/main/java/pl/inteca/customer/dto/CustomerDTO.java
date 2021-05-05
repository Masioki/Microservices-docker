package pl.inteca.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Customer
 *
 * @see pl.inteca.customer.domain.Customer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private String pesel;
    private String firstName;
    private String surname;
    private long creditID;
}
