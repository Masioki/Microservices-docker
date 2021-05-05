package pl.inteca.credit.dto;


import lombok.Data;

/**
 * DTO with format of data returned by Customer microservice
 */
@Data
public class CustomerDTO {
    private long creditID;
    private String firstName;
    private String pesel;
    private String surname;
}
