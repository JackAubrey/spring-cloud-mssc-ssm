package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class PreAuthAction extends AbstractActionSupport {
    Random rnd = new Random();

    /**
     * Execute action with a {@link StateContext}.
     *
     * @param context the state context
     */
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.info("BEAN PreAuthAction | PreAuth was called!!!");

        if( rnd.nextInt(10) <8 ) {
            log.info("BEAN PreAuthAction | Approved!!!");
            sendEvent(context, PaymentEvent.PRE_AUTH_APPROVED);
        } else {
            log.info("BEAN PreAuthAction | Decline!!! No Credit !!!");
            sendEvent(context, PaymentEvent.PRE_AUTH_DECLINED);
        }
    }
}
