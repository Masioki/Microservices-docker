package pl.inteca.credit.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pl.inteca.credit.dto.ProductDTO;
import pl.inteca.credit.exception.CreditException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service / API Client for Product microservice
 */
@Service
public class ProductService {

    private final RestTemplate restTemplate = new RestTemplate();
    // properties from application.properties
    @Value("${services.product.base-url}")
    private String baseUrl;
    @Value("${services.product.endpoints.get-products}")
    private String getProductsEndpoint;
    @Value("${services.product.endpoints.create}")
    private String createProductEndpoint;
    @Value("${services.product.endpoints.remove}")
    private String removeProductEndpoint;

    @PostConstruct
    public void init() {
        // dont throw exception on response status different from 2xx
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return !clientHttpResponse.getStatusCode().is2xxSuccessful();
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

            }
        });
    }

    /**
     * @param creditIDs IDs for which productss should be returned
     * @return All products which has credits with id in creditIDs
     * @throws Exception request / connection error
     */
    public List<ProductDTO> getProducts(List<Long> creditIDs) throws Exception {
        HttpEntity<List<Long>> request = new HttpEntity<>(creditIDs);
        ResponseEntity<ProductDTO[]> resultEntity = restTemplate.postForEntity(baseUrl + getProductsEndpoint, request, ProductDTO[].class);

        // on success return body
        if (resultEntity.getStatusCode().is2xxSuccessful()) {
            if (resultEntity.hasBody()) return Arrays.asList(resultEntity.getBody());
            else return new ArrayList<>();
        }

        // unidentified error, get all method of Product microservice should always return OK
        throw new Exception();
    }

    /**
     * Creates customer from provided data
     *
     * @param productDTO data for new product
     * @return product id
     * @throws CreditException probably wrong data format
     * @throws Exception       request / connection error
     */
    public long createProduct(ProductDTO productDTO) throws CreditException, Exception {
        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO);
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(baseUrl + createProductEndpoint, request, String.class);

        // if status OK, then body contains ID
        if (resultEntity.getStatusCode().is2xxSuccessful() && resultEntity.hasBody()) {
            return Long.parseLong(resultEntity.getBody());
        } else if (resultEntity.getStatusCode().is4xxClientError()) { // probably wrong data format
            if (resultEntity.hasBody()) throw new CreditException(resultEntity.getBody());
            throw new CreditException("Bad request, check data format!");
        }
        // unidentified error
        throw new Exception("Create product error");
    }

    /**
     * Removes specified product
     *
     * @param productID product id
     */
    public void remove(long productID) {
        restTemplate.delete(baseUrl + removeProductEndpoint + "/" + productID, String.class);
    }
}
