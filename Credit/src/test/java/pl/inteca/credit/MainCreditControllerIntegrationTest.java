package pl.inteca.credit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.inteca.credit.controllers.MainCreditController;
import pl.inteca.credit.dto.CreditDTO;
import pl.inteca.credit.dto.CreditInfoDTO;
import pl.inteca.credit.dto.CustomerDTO;
import pl.inteca.credit.dto.ProductDTO;
import pl.inteca.credit.repositories.CreditRepository;
import pl.inteca.credit.services.MainService;

import javax.xml.bind.ValidationException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(MainCreditController.class)
public class MainCreditControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditRepository creditRepository;

    @MockBean
    private MainService service;

    @Test
    public void getAllCreditsTest() throws Exception {
        List<CreditInfoDTO> credits = List.of();
        given(service.getAllCredits()).willReturn(credits);

        // will all credits be returned?
        mockMvc.perform(get("/api/get/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(credits)));
    }

    @Test
    public void createCreditTest() throws Exception {
        CreditInfoDTO credit = new CreditInfoDTO();
        credit.setCredit(new CreditDTO());
        credit.setCustomer(new CustomerDTO());
        credit.setProduct(new ProductDTO());

        // given data with wrong format
        given(service.createCredit(credit)).willThrow(ValidationException.class);
        // will 4xx status be returned?
        mockMvc.perform(
                post("/api/create")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credit)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        // given proper data
        given(service.createCredit(any())).willReturn(1L);
        // will ID be returned
        mockMvc.perform(
                post("/api/create")
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(credit)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
