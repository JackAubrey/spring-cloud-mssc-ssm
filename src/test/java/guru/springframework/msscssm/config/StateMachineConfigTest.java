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

@SpringBootTest
class StateMachineConfigTest {
    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> sm  = factory.getStateMachine(UUID.randomUUID());
        sm.startReactively();

        System.out.println("State Machine State before: " + sm.getState());
        Message<PaymentEvent> event = MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build();
        sm.sendEvent(Mono.just(event)).subscribe(c -> {
            System.out.println("State Machine State Completable: " + c.getRegion().getState());
        });
        System.out.println("State Machine State after: " + sm.getState());
    }
}