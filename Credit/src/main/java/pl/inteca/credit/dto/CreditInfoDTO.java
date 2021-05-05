package pl.inteca.credit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * DTO for transfer of all credit-connected data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditInfoDTO {
    @NotNull(message = "Credit data cannot be empty")
    @Valid
    private CreditDTO credit;

    @NotNull(message = "Customer data are necessary")
    @Valid
    private CustomerDTO customer;

    @NotNull(message = "Product data cannot be empty")
    @Valid
    private ProductDTO product;

}
