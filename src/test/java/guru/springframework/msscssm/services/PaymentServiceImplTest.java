package guru.springframework.msscssm.services;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .amount(new BigDecimal("12.99"))
                .build();
    }

    @Transactional
    @RepeatedTest(30)
    void testPreAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW, savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
        assertThat(sm.getState().getId(), anyOf(is(PaymentState.PRE_AUTH), is(PaymentState.PRE_AUTH_ERROR)));

        Payment preAuthPayment = paymentService.getPayment(savedPayment.getId());
        if( sm.getState().getId() == PaymentState.PRE_AUTH) {
            assertEquals(PaymentState.PRE_AUTH, preAuthPayment.getState());
        } else {
            assertEquals(PaymentState.PRE_AUTH_ERROR, preAuthPayment.getState());
        }
    }

    @Transactional
    @RepeatedTest(30)
    void testAuth() {
        Payment savedPayment = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(savedPayment.getId());
        assertThat(preAuthSM.getState().getId(), anyOf(is(PaymentState.PRE_AUTH), is(PaymentState.PRE_AUTH_ERROR)));

        if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
            System.out.println("Payment is Pre Authorized");
            StateMachine<PaymentState, PaymentEvent> authSM = paymentService.authorizePayment(savedPayment.getId());

            assertThat(authSM.getState().getId(), anyOf(is(PaymentState.AUTH), is(PaymentState.AUTH_ERROR)));
            System.out.println("Result of Auth: " + authSM.getState().getId());
        } else {
            System.err.println("Payment failed pre-auth...");
        }
    }
}