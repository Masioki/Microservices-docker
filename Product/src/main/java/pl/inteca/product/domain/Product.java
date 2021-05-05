package pl.inteca.product.domain;

import lombok.*;
import pl.inteca.product.dto.ProductDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Product entity
 */
@Entity
@Getter
@ToString
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private long ID;

    @Setter
    private long creditID;

    @Setter
    @Size(min = 5, message = "Product name should be longer than 4 characters")
    private String productName;

    @Setter
    @Min(value = 1, message = "Product value must be more than zero")
    private int value;

    public Product(long creditID, String name, int value) {
        this.creditID = creditID;
        this.productName = name;
        this.value = value;
    }

    /**
     * @param dto Product data
     * @return Product created from data in dto
     */
    public static Product newFromDTO(@NonNull ProductDTO dto) {
        return new Product(dto.getCreditID(), dto.getProductName(), dto.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return ID == product.ID && creditID == product.creditID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, creditID);
    }
}
