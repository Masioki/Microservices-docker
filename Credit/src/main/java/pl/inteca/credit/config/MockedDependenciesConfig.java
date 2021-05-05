package pl.inteca.credit.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pl.inteca.credit.services.CustomerService;
import pl.inteca.credit.services.ProductService;

/**
 * Test configuration which replaces other microservices with mocks
 */
@Profile({"test", "dev"})
@Configuration
public class MockedDependenciesConfig {

    @Bean
    @Primary
    public CustomerService customerService() {
        return Mockito.mock(CustomerService.class);
    }

    @Bean
    @Primary
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }
}
