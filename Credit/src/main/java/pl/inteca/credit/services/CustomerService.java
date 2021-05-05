package pl.inteca.credit.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pl.inteca.credit.dto.CustomerDTO;
import pl.inteca.credit.exception.CreditException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service / API Client for Customer microservice
 */
@Service
public class CustomerService {

    private final RestTemplate restTemplate = new RestTemplate();
    // properties from application.properties
    @Value("${services.customer.base-url}")
    private String baseUrl;
    @Value("${services.customer.endpoints.get-customers}")
    private String getCustomersEndpoint;
    @Value("${services.customer.endpoints.create}")
    private String createCustomerEndpoint;
    @Value("${services.customer.endpoints.remove}")
    private String removeCustomerEndpoint;

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
     * @param creditIDs IDs for which customers should be returned
     * @return All customers who has credits with id in creditIDs
     * @throws Exception request / connection error
     */
    public List<CustomerDTO> getCustomers(List<Long> creditIDs) throws Exception {
        HttpEntity<List<Long>> request = new HttpEntity<>(creditIDs);
        ResponseEntity<CustomerDTO[]> resultEntity = restTemplate.postForEntity(baseUrl + getCustomersEndpoint, request, CustomerDTO[].class);

        // on success return body
        if (resultEntity.getStatusCode().is2xxSuccessful()) {
            if (resultEntity.hasBody()) return Arrays.asList(resultEntity.getBody());
            else return new ArrayList<>();
        }
        // unidentified error
        throw new Exception("Get customers error");
    }

    /**
     * Creates customer from provided data
     *
     * @param customerDTO data for customer
     * @return customer ID
     * @throws CreditException probably wrong data format
     * @throws Exception       request / connection error
     */
    public void createCustomer(CustomerDTO customerDTO) throws CreditException, Exception {
        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO);
        ResponseEntity<String> resultEntity = restTemplate.postForEntity(baseUrl + createCustomerEndpoint, request, String.class);

        if (!resultEntity.getStatusCode().is2xxSuccessful()) {
            // probably wrong data format
            if (resultEntity.getStatusCode().is4xxClientError()) {
                if (resultEntity.hasBody()) throw new CreditException(resultEntity.getBody());
                throw new CreditException("Wrong data format!");
            }
            // unidentified error
            throw new Exception("Create customer error");
        }
    }

}
