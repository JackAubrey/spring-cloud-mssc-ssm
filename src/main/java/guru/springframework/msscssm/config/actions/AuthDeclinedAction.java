package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthDeclinedAction extends AbstractActionSupport {
    /**
     * Execute action with a {@link StateContext}.
     *
     * @param context the state context
     */
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("BEAN AuthDeclinedAction | Auth Decline!!! No Credit !!!");
    }
}
