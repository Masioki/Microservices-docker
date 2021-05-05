package pl.inteca.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.inteca.product.controller.MainProductController;
import pl.inteca.product.domain.Product;
import pl.inteca.product.dto.ProductDTO;
import pl.inteca.product.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Whole application test
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductApplicationTests {

    @Autowired
    private MainProductController controller;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private List<ProductDTO> productList;

    @BeforeAll
    public void populateDB() {
        List<Product> temp = List.of(
                new Product(1, "Name one", 1),
                new Product(2, "Name two", 2),
                new Product(3, "Name three", 3),
                new Product(4, "Name four", 4),
                new Product(5, "Name five", 5)
        );
        repository.saveAll(temp);
        this.productList = temp.stream().map(ProductDTO::new).collect(Collectors.toList());
    }

    @Test
    @Order(0)
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    @Order(1)
    public void getAllProductsTest() throws Exception {
        List<Long> testIDs = List.of(1L, 3L, 5L);
        List<ProductDTO> properResult = List.of(productList.get(0), productList.get(2), productList.get(4));

        // are proper products returned?
        mockMvc.perform(
                post("/api/get/all")
                        .content(new ObjectMapper().writeValueAsString(testIDs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(properResult)));
    }


    @Test
    @Order(2)
    public void createProductTest() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setCreditID(10);
        product.setProductName("shrt");
        product.setValue(1);

        // too short product name
        mockMvc.perform(
                post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        product.setProductName("long enough");
        product.setValue(0);

        // to small product value
        mockMvc.perform(
                post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        product.setValue(1);

        // proper product format, should be created
        mockMvc.perform(
                post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("6"));

        // was new product saved in database?
        assertFalse(repository.findById(6L).isEmpty());
    }

    @Test
    @Order(3)
    public void removeProductTest() throws Exception {
        long productID = 1;
        // remove product
        mockMvc.perform(delete("/api/remove/" + productID))
                .andDo(print())
                .andExpect(status().isOk());

        // was product removed?
        mockMvc.perform(
                post("/api/get/all")
                        .content(new ObjectMapper().writeValueAsString(Collections.singletonList(1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(List.of())));

        // should return ok even if productID doesnt exist
        mockMvc.perform(delete("/api/remove/" + productID))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
