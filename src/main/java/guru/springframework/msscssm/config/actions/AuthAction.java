package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class AuthAction extends AbstractActionSupport {
    Random rnd = new Random();

    /**
     * Execute action with a {@link StateContext}.
     *
     * @param context the state context
     */
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("BEAN AuthAction | Auth was called!!!");

        if( rnd.nextInt(10) <8 ) {
            log.info("BEAN AuthAction | Auth Approved!!!");
            sendEvent(context, PaymentEvent.AUTH_APPROVED);
        } else {
            log.info("BEAN AuthAction | Auth Decline!!! No Credit !!!");
            sendEvent(context, PaymentEvent.AUTH_DECLINED);
        }
    }
}
