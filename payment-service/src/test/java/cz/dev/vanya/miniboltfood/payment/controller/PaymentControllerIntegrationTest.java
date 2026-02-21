package cz.dev.vanya.miniboltfood.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import cz.dev.vanya.miniboltfood.payment.handler.GlobalPaymentServiceExceptionHandler;
import cz.dev.vanya.miniboltfood.payment.repository.PaymentRepository;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceEndpointUtils;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceConstantHolder;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceObjectProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
class PaymentControllerIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MockMvc mockMvc;

    @Autowired
    private PaymentController paymentController;

    @Autowired
    private GlobalPaymentServiceExceptionHandler globalPaymentServiceExceptionHandler;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(globalPaymentServiceExceptionHandler)
                .build();
        paymentRepository.deleteAll();
    }

    @Test
    void createPayment_persistsPaymentAndReturnsCreatedResponse() throws Exception {
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();

        mockMvc.perform(post(PaymentServiceEndpointUtils.CREATE_PAYMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.orderId").value(PaymentServiceConstantHolder.ORDER_ID))
                .andExpect(jsonPath("$.amount").value(PaymentServiceConstantHolder.AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.paymentMethod").value(PaymentServiceConstantHolder.PAYMENT_METHOD_DTO.name()))
                .andExpect(jsonPath("$.paymentStatus",
                        anyOf(is(PaymentStatus.PAYMENT_SUCCEEDED.name()), is(PaymentStatus.PAYMENT_FAILED.name()))));

        final Optional<Payment> persistedPayment = paymentRepository.findByOrderId(PaymentServiceConstantHolder.ORDER_ID);
        assertThat(persistedPayment).isPresent();
        assertEquals(PaymentServiceConstantHolder.AMOUNT, persistedPayment.get().getAmount());
        assertEquals(PaymentServiceConstantHolder.PAYMENT_METHOD, persistedPayment.get().getPaymentMethod());
        assertThat(persistedPayment.get().getPaymentStatus())
                .isIn(PaymentStatus.PAYMENT_SUCCEEDED, PaymentStatus.PAYMENT_FAILED);
    }

    @Test
    void createPayment_whenCalledTwiceForSameOrder_returnsExistingPaymentAndKeepsSingleRow() throws Exception {
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();

        final CreatePaymentResponseDto firstResponse = performCreatePayment(request);
        final CreatePaymentResponseDto secondResponse = performCreatePayment(request);

        assertEquals(firstResponse.id(), secondResponse.id());
        assertEquals(firstResponse.amount(), secondResponse.amount());
        assertEquals(firstResponse.orderId(), secondResponse.orderId());
        assertEquals(firstResponse.paymentStatus(), secondResponse.paymentStatus());
        assertEquals(firstResponse.paymentMethod(), secondResponse.paymentMethod());

        assertEquals(1, paymentRepository.count());
    }

    private CreatePaymentResponseDto performCreatePayment(final CreatePaymentRequestDto request) throws Exception {
        final MvcResult mvcResult = mockMvc.perform(post(PaymentServiceEndpointUtils.CREATE_PAYMENT_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsByteArray(), CreatePaymentResponseDto.class);
    }
}
