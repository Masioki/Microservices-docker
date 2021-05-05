package pl.inteca.product;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.inteca.product.controller.MainProductController;
import pl.inteca.product.domain.Product;
import pl.inteca.product.dto.ProductDTO;
import pl.inteca.product.service.MainService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(MainProductController.class)
public class MainProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MainService service;

    @Test
    public void getAllProductsTest() throws Exception {
        List<ProductDTO> products = List.of(
                new Product(1, "Name one", 1),
                new Product(2, "Name two", 2),
                new Product(3, "Name three", 3),
                new Product(4, "Name four", 4),
                new Product(5, "Name five", 5)
        ).stream().map(ProductDTO::new).collect(Collectors.toList());
        List<ProductDTO> productsSublist = products.subList(0, 2);

        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L);
        List<Long> idSubList = ids.subList(0, 2);

        given(service.getProductsByCreditId(ids)).willReturn(products);
        given(service.getProductsByCreditId(idSubList)).willReturn(products.subList(0, 2));

        // are all proper products returned?
        mockMvc.perform(
                post("/api/get/all")
                        .content(new ObjectMapper().writeValueAsString(ids))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(products)));

        mockMvc.perform(
                post("/api/get/all")
                        .content(new ObjectMapper().writeValueAsString(idSubList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(productsSublist)));
    }


    @Test
    public void createProductTest() throws Exception {
        ProductDTO product = new ProductDTO();
        product.setCreditID(10);
        product.setProductName("long enough");
        product.setValue(1);

        given(service.createProduct(product)).willReturn(1L);

        // should return created product ID
        mockMvc.perform(
                post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void removeProductTest() throws Exception {
        long productID = 1;
        // remove product should return OK
        mockMvc.perform(delete("/api/remove/" + productID))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
