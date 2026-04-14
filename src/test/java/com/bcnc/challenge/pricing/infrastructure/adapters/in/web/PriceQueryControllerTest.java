package com.bcnc.challenge.pricing.infrastructure.adapters.in.web;

import com.bcnc.challenge.pricing.application.exceptions.ApplicablePriceNotFoundException;
import com.bcnc.challenge.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.bcnc.challenge.pricing.application.result.ApplicablePriceResult;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.filter.CorrelationIdFilter;
import com.bcnc.challenge.pricing.infrastructure.adapters.in.web.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(PriceQueryController.class)
@Import({GlobalExceptionHandler.class, CorrelationIdFilter.class})
class PriceQueryControllerTest {

    private static final String ENDPOINT = "/api/v1/prices";
    private static final String SUCCESS_MESSAGE = "Applicable price retrieved successfully";
    private static final String BAD_REQUEST_MESSAGE = "Invalid request";
    private static final String NOT_FOUND_MESSAGE = "Resource not found";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
    private static final String ERROR_TYPE = "about:blank";
    private static final int BAD_REQUEST_STATUS = 400;
    private static final int NOT_FOUND_STATUS = 404;
    private static final int INTERNAL_SERVER_ERROR_STATUS = 500;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetApplicablePriceUseCase getApplicablePriceUseCase;

    @Test
    void shouldReturnApplicablePriceWhenRequestIsValid(CapturedOutput output) throws Exception {
        var response = new ApplicablePriceResult(
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

        MvcResult mvcResult = mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
                .andExpect(jsonPath("$.correlationId", notNullValue()))
                .andExpect(jsonPath("$.payload.productId").value(35455))
                .andExpect(jsonPath("$.payload.brandId").value(1))
                .andExpect(jsonPath("$.payload.priceList").value(1))
                .andExpect(jsonPath("$.payload.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.payload.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.payload.price").value(35.50))
                .andExpect(jsonPath("$.error").doesNotExist())
                .andReturn();

        verify(getApplicablePriceUseCase).execute(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L
        );

        String correlationId = mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertControllerSuccessLogs(output, correlationId);
    }

    @Test
    void shouldReturnBadRequestWhenApplicationDateIsMissing(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("productId", "35455")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: applicationDate").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: applicationDate"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsMissing(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: productId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: productId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdIsMissing(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "35455")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: brandId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: brandId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenApplicationDateIsInvalid(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "14-06-2020 10:00:00")
                .param("productId", "35455")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: applicationDate").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: applicationDate"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsInvalid(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "abc")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: productId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: productId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdIsInvalid(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "35455")
                .param("brandId", "abc")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: brandId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: brandId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsNegative(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "-1")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: productId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: productId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdIsNegative(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "35455")
                .param("brandId", "-1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: brandId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: brandId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenProductIdIsZero(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "0")
                .param("brandId", "1")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: productId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: productId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenBrandIdIsZero(CapturedOutput output) throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("applicationDate", "2020-06-14T10:00:00")
                .param("productId", "35455")
                .param("brandId", "0")
                .accept(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = assertBadRequest(result, "Invalid value for parameter: brandId").andReturn();
        assertErrorLog(
                output,
                mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER),
                "Invalid value for parameter: brandId"
        );

        verifyNoInteractions(getApplicablePriceUseCase);
    }

    @Test
    void shouldReturnNotFoundWhenNoApplicablePriceExistsForGivenParameters(CapturedOutput output) throws Exception {
        when(getApplicablePriceUseCase.execute(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L
        )).thenThrow(new ApplicablePriceNotFoundException(
                "No applicable price found for the given parameters"
        ));

        MvcResult mvcResult = mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.correlationId", notNullValue()))
                .andExpect(jsonPath("$.payload").doesNotExist())
                .andExpect(jsonPath("$.error.type").value(ERROR_TYPE))
                .andExpect(jsonPath("$.error.title").value(NOT_FOUND_MESSAGE))
                .andExpect(jsonPath("$.error.status").value(NOT_FOUND_STATUS))
                .andExpect(jsonPath("$.error.detail").value("No applicable price found for the given parameters"))
                .andExpect(jsonPath("$.error.instance").value(ENDPOINT))
                .andReturn();

        String correlationId = mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertNotFoundLog(output, correlationId, "No applicable price found for the given parameters");
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedRuntimeExceptionOccurs(CapturedOutput output) throws Exception {
        MvcResult mvcResult = assertInternalServerError(
                new RuntimeException("Database unavailable")
        ).andReturn();

        String correlationId = mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertUnexpectedErrorLog(output, correlationId);
    }

    @Test
    void shouldReturnInternalServerErrorWhenNullPointerExceptionOccurs(CapturedOutput output) throws Exception {
        MvcResult mvcResult = assertInternalServerError(
                new NullPointerException("Unexpected null in business rule")
        ).andReturn();

        String correlationId = mvcResult.getResponse().getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertUnexpectedErrorLog(output, correlationId);
    }

    @Test
    void shouldReturnProvidedCorrelationIdInResponseHeader() throws Exception {
        when(getApplicablePriceUseCase.execute(any(), anyLong(), anyLong()))
                .thenReturn(new ApplicablePriceResult(
                        35455L,
                        1L,
                        1,
                        LocalDateTime.of(2020, 6, 14, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                        new BigDecimal("35.50")
                ));

        mockMvc.perform(get("/api/v1/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .header("X-Correlation-Id", "test-correlation-id-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-Id", "test-correlation-id-123"));
    }

    private ResultActions assertBadRequest(ResultActions result, String detail) throws Exception {
        return result
                .andExpect(status().isBadRequest())
                .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath("$.correlationId", notNullValue()))
                .andExpect(jsonPath("$.payload").doesNotExist())
                .andExpect(jsonPath("$.error.type").value(ERROR_TYPE))
                .andExpect(jsonPath("$.error.title").value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath("$.error.status").value(BAD_REQUEST_STATUS))
                .andExpect(jsonPath("$.error.detail").value(detail))
                .andExpect(jsonPath("$.error.instance").value(ENDPOINT));
    }

    private ResultActions assertInternalServerError(Exception exception) throws Exception {
        when(getApplicablePriceUseCase.execute(
                LocalDateTime.of(2020, 6, 14, 10, 0),
                35455L,
                1L
        )).thenThrow(exception);

        return mockMvc.perform(get(ENDPOINT)
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER))
                .andExpect(jsonPath("$.message").value(INTERNAL_SERVER_ERROR_MESSAGE))
                .andExpect(jsonPath("$.correlationId", notNullValue()))
                .andExpect(jsonPath("$.payload").doesNotExist())
                .andExpect(jsonPath("$.error.type").value(ERROR_TYPE))
                .andExpect(jsonPath("$.error.title").value(INTERNAL_SERVER_ERROR_MESSAGE))
                .andExpect(jsonPath("$.error.status").value(INTERNAL_SERVER_ERROR_STATUS))
                .andExpect(jsonPath("$.error.detail").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.error.instance").value(ENDPOINT));
    }

    private void assertControllerSuccessLogs(CapturedOutput output, String correlationId) {
        String logs = output.getOut();

        assertTrue(logs.contains("Price query received."), "Expected entry log was not found");
        assertTrue(logs.contains("Price query processed successfully."), "Expected success log was not found");
        assertTrue(logs.contains("correlationId=" + correlationId), "Expected correlationId was not found in success logs");
        assertTrue(logs.contains("productId=35455"), "Expected productId was not found in success logs");
        assertTrue(logs.contains("brandId=1"), "Expected brandId was not found in success logs");
        assertTrue(logs.contains("priceList=1"), "Expected priceList was not found in success logs");
    }

    private void assertErrorLog(CapturedOutput output, String correlationId, String detail) {
        String logs = output.getOut();

        assertTrue(logs.contains("Request validation failed."), "Expected validation error log was not found");
        assertTrue(logs.contains("correlationId=" + correlationId), "Expected correlationId was not found in error logs");
        assertTrue(logs.contains("detail=" + detail), "Expected error detail was not found in logs");
    }

    private void assertUnexpectedErrorLog(CapturedOutput output, String correlationId) {
        String logs = output.getOut();

        assertTrue(logs.contains("Unexpected error while processing request."),
                "Expected unexpected error log was not found");
        assertTrue(logs.contains("correlationId=" + correlationId),
                "Expected correlationId was not found in unexpected error logs");
    }

    private void assertNotFoundLog(CapturedOutput output, String correlationId, String detail) {
        String logs = output.getOut();

        assertTrue(logs.contains("Applicable price not found."),
                "Expected not found log was not found");
        assertTrue(logs.contains("correlationId=" + correlationId),
                "Expected correlationId was not found in not found logs");
        assertTrue(logs.contains("detail=" + detail),
                "Expected detail was not found in not found logs");
    }
}