package pl.inteca.credit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.inteca.credit.domain.Credit;
import pl.inteca.credit.dto.CreditDTO;
import pl.inteca.credit.dto.CreditInfoDTO;
import pl.inteca.credit.dto.CustomerDTO;
import pl.inteca.credit.dto.ProductDTO;
import pl.inteca.credit.repositories.CreditRepository;
import pl.inteca.credit.services.MainService;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
public class MainServiceTest {

    @Autowired
    private MainService service;

    @MockBean
    private CreditRepository creditRepository;


    @Test
    public void getAllCreditsTest() throws Exception {
        // sample credits
        List<Credit> credits = new ArrayList<>();
        Credit credit = new Credit();
        credit.setID(1L);
        credits.add(credit);

        credit = new Credit();
        credit.setID(2L);
        credits.add(credit);

        credit = new Credit();
        credit.setID(3L);
        credits.add(credit);
        List<Long> ids = List.of(1L, 2L, 3L);

        // given credits returned by database/other services
        given(creditRepository.findAll()).willReturn(credits);

        // will returned DTO with all credits
        List<Long> foundIds = service.getAllCredits().stream()
                .map(c -> c.getCredit().getID())
                .collect(Collectors.toList());

        assertThat(foundIds).isEqualTo(ids);
    }

    @Test
    public void createCreditTest() {
        CreditDTO creditDTO = new CreditDTO();
        CreditInfoDTO credit = new CreditInfoDTO();
        credit.setCredit(creditDTO);
        credit.setCustomer(new CustomerDTO());
        credit.setProduct(new ProductDTO());

        // given credit with wrong name should throw exception
        credit.getCredit().setCreditName("");
        assertThrows(ValidationException.class, () -> service.createCredit(credit));

        // given proper credit data should not throw and save to DB
        credit.getCredit().setCreditName("long enough");
        assertDoesNotThrow(() -> service.createCredit(credit));
        verify(creditRepository).save(any());
    }
}
