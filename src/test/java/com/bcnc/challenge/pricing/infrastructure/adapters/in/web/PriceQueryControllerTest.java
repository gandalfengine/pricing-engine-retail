package com.bcnc.challenge.pricing.infrastructure.adapters.in.web;

import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.response.ApplicablePriceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceQueryController.class)
class PriceQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetApplicablePriceUseCase getApplicablePriceUseCase;

    @Test
    void shouldReturnApplicablePriceWhenRequestIsValid() throws Exception {
        var response = new ApplicablePriceResponse(
                35455L,
                1L,
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50")
        );

        when(getApplicablePriceUseCase.execute(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));

        verify(getApplicablePriceUseCase).execute(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L
        );
    }

    @Test
    void shouldReturnBadRequestWhenApplicationDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenApplicationDateIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "14-06-2020 10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "abc")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldInvokeUseCaseWithCorrectParameters() throws Exception {
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        var response = new ApplicablePriceResponse(
                35455L,
                1L,
                1,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50")
        );

        when(getApplicablePriceUseCase.execute(applicationDate, 35455L, 1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk());

        verify(getApplicablePriceUseCase).execute(applicationDate, 35455L, 1L);
    }
}