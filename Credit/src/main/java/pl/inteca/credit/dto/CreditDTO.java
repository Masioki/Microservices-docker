package pl.inteca.credit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.inteca.credit.domain.Credit;

/**
 * DTO for Credit
 *
 * @see Credit
 */
@Data
@NoArgsConstructor
public class CreditDTO {
    private long ID;
    private String creditName;

    public CreditDTO(Credit credit) {
        if (credit != null) {
            this.creditName = credit.getCreditName();
            this.ID = credit.getID();
        }
    }

}
