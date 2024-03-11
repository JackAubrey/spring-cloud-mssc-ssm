package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StateMachineConfigTest {
    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> sm  = factory.getStateMachine(UUID.randomUUID());
        sm.startReactively().subscribe();

        System.out.println("State Machine State: " + sm.getState().getId());
        assertEquals(PaymentState.NEW, sm.getState().getId());

        Message<PaymentEvent> event = MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build();
        sm.sendEvent(Mono.just(event)).subscribe();
        System.out.println("State Machine State: " + sm.getState().getId());
        assertEquals(PaymentState.NEW, sm.getState().getId());

        Message<PaymentEvent> event2 = MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).build();
        sm.sendEvent(Mono.just(event2)).subscribe();
        System.out.println("State Machine State: " + sm.getState().getId());
        assertEquals(PaymentState.PRE_AUTH, sm.getState().getId());
    }
}