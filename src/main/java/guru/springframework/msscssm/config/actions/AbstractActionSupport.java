package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentService;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import reactor.core.publisher.Mono;

abstract class AbstractActionSupport implements Action<PaymentState, PaymentEvent> {
    protected void sendEvent(StateContext<PaymentState, PaymentEvent> context, PaymentEvent preAuthApproved) {
        Message<PaymentEvent> message = getPaymentEventMessage(context, preAuthApproved);
        context.getStateMachine()
                .sendEvent(messageAsMono(message))
                .subscribe();
    }

    protected Mono<Message<PaymentEvent>> messageAsMono(Message<PaymentEvent> message) {
        return Mono.just(message);
    }

    protected Message<PaymentEvent> getPaymentEventMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader(PaymentService.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER))
                .build();
    }
}
