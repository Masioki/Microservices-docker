package pl.inteca.customer.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.inteca.customer.config.validation.Pesel;
import pl.inteca.customer.dto.CustomerDTO;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * Customer entity
 */
@Entity
@Getter
@Setter
@ToString
public class Customer {

    @Id
    @Pesel(message = "Pesel format is 11 numerical characters")
    private String pesel;

    @NotBlank(message = "Firstname cannot be empty")
    private String firstName;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;


    @CollectionTable(name = "creditId_mapping")
    @ElementCollection
    private Set<Long> creditsIds;

    public Customer() {
        this.creditsIds = new HashSet<>();
    }


    /**
     * @param dto Customer data
     * @return Customer object created based on data passed in dto
     */
    public static Customer newFromDTO(CustomerDTO dto) {
        Customer result = new Customer();
        result.setFirstName(dto.getFirstName());
        result.setSurname(dto.getSurname());
        result.setPesel(dto.getPesel());
        return result;
    }

    /**
     * Assign credit to customer
     *
     * @param creditID credit ID
     */
    public void addCredit(long creditID) {
        this.creditsIds.add(creditID);
    }
}
