package pl.inteca.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.product.domain.Product;
import pl.inteca.product.dto.ProductDTO;
import pl.inteca.product.repository.ProductRepository;
import pl.inteca.product.service.MainService;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@SpringBootTest
public class MainServiceTest {

    @Autowired
    private MainService service;

    @MockBean
    private ProductRepository repository;

    @Test
    public void createProductTest() {
        ProductDTO product = new ProductDTO();
        product.setCreditID(0);
        product.setProductName("shrt");
        product.setValue(1);

        // too short name
        String mes = assertThrows(ValidationException.class, () -> service.createProduct(product)).getMessage();
        assertEquals(mes, "Product name should be longer than 4 characters");

        product.setProductName("long enough");
        product.setValue(0);

        // value too small
        mes = assertThrows(ValidationException.class, () -> service.createProduct(product)).getMessage();
        assertEquals(mes, "Product value must be more than zero");

        // ok
        product.setValue(1);
        assertDoesNotThrow(() -> service.createProduct(product));
    }

    @Test
    public void getProductsByCreditIdTest() {
        List<Product> products = List.of(
                new Product(1, "", 1),
                new Product(2, "", 1),
                new Product(3, "", 1),
                new Product(4, "", 1)
        );
        List<ProductDTO> dtos = products.stream().map(ProductDTO::new).collect(Collectors.toList());
        List<Long> ids = List.of(1L, 2L, 3L, 4L);

        given(repository.findByCreditIDIn(ids)).willReturn(products);

        assertEquals(service.getProductsByCreditId(ids), dtos);
    }
}
