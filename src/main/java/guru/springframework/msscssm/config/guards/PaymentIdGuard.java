package guru.springframework.msscssm.config.guards;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentService;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {

    /**
     * Evaluate a guard condition.
     *
     * @param context the state context
     * @return true, if guard evaluation is successful, false otherwise.
     */
    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
        return context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER) != null;
    }
}
