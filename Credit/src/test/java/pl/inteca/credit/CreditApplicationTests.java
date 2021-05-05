package pl.inteca.credit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.credit.controllers.MainCreditController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CreditApplicationTests {

    @Autowired
    private MainCreditController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
