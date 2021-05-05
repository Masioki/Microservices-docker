package pl.inteca.credit.domain;

import lombok.*;
import pl.inteca.credit.dto.CreditDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Credit entity
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Credit {
    @Id
    @GeneratedValue
    private long ID;

    @Size(min = 5, max = 200, message = "Credit name should have more than 5 and less than 200 characters")
    private String creditName;


    public static Credit newFromDTO(@NonNull CreditDTO dto) {
        Credit result = new Credit();
        result.setCreditName(dto.getCreditName());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credit)) return false;
        Credit credit = (Credit) o;
        return ID == credit.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
