package guru.springframework.msscssm.config;

import guru.springframework.msscssm.config.actions.*;
import guru.springframework.msscssm.config.guards.PaymentIdGuard;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
//    Random rnd = new Random();
    private final PreAuthAction preAuthAction;
    private final PreAuthApprovedAction preAuthApprovedAction;
    private final PreAuthDeclinedAction preAuthDeclinedAction;
    private final AuthAction authAction;
    private final AuthApprovedAction authApprovedAction;
    private final AuthDeclinedAction authDeclinedAction;
    private final PaymentIdGuard paymentIdGuard;

    public StateMachineConfig(PreAuthAction preAuthAction, PreAuthApprovedAction preAuthApprovedAction, PreAuthDeclinedAction preAuthDeclinedAction, AuthAction authAction, AuthApprovedAction authApprovedAction, AuthDeclinedAction authDeclinedAction, PaymentIdGuard paymentIdGuard) {
        this.preAuthAction = preAuthAction;
        this.preAuthApprovedAction = preAuthApprovedAction;
        this.preAuthDeclinedAction = preAuthDeclinedAction;
        this.authAction = authAction;
        this.authApprovedAction = authApprovedAction;
        this.authDeclinedAction = authDeclinedAction;
        this.paymentIdGuard = paymentIdGuard;
    }

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                    .action(preAuthAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                    .action(preAuthApprovedAction)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                    .action(preAuthDeclinedAction)
                //preauth to auth
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                    .action(authAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
                    .action(authApprovedAction)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED)
                    .action(authDeclinedAction);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("State Changed (from {} to {})", from == null ? null : from.getId(), to.getId());
            }
        };

        config.withConfiguration().listener(adapter);
    }

//    Guard<PaymentState, PaymentEvent> paymentIdGuard() {
//        return context -> context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER) != null;
//    }

    /*Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
          log.info("PreAuth was called!!!");

          if( rnd.nextInt(10) <8 ) {
              log.info("Approved!!!");
              sendEvent(context, PaymentEvent.PRE_AUTH_APPROVED);
          } else {
              log.info("Decline!!! No Credit !!!");
              sendEvent(context, PaymentEvent.PRE_AUTH_DECLINED);
          }
        };
    }*/

//    Action<PaymentState, PaymentEvent> authAction() {
//        return context -> {
//            log.info("Auth was called!!!");
//
//            if( rnd.nextInt(10) <8 ) {
//                log.info("Auth Approved!!!");
//                sendEvent(context, PaymentEvent.AUTH_APPROVED);
//            } else {
//                log.info("Auth Decline!!! No Credit !!!");
//                sendEvent(context, PaymentEvent.AUTH_DECLINED);
//            }
//        };
//    }

//    private static void sendEvent(StateContext<PaymentState, PaymentEvent> context, PaymentEvent preAuthApproved) {
//        Message<PaymentEvent> message = getPaymentEventMessage(context, preAuthApproved);
//        context.getStateMachine()
//                .sendEvent(messageAsMono(message))
//                .subscribe();
//    }
//
//    private static Mono<Message<PaymentEvent>> messageAsMono(Message<PaymentEvent> message) {
//        return Mono.just(message);
//    }
//
//    private static Message<PaymentEvent> getPaymentEventMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent event) {
//        return MessageBuilder
//                .withPayload(event)
//                .setHeader(PaymentService.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentService.PAYMENT_ID_HEADER))
//                .build();
//    }
}
