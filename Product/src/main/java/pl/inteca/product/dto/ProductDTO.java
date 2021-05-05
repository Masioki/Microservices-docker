package pl.inteca.product.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.inteca.product.domain.Product;

/**
 * DTO for Product
 *
 * @see Product
 */
@Data
@NoArgsConstructor
public class ProductDTO {
    private long creditID;
    private String productName;
    private int value;

    public ProductDTO(Product product) {
        this.creditID = product.getCreditID();
        this.productName = product.getProductName();
        this.value = product.getValue();
    }
}
