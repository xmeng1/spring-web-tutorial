package science.mengxin.spring.web.tutorial.akka.actor;

import akka.actor.UntypedActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import science.mengxin.spring.web.tutorial.domain.LoginMessage;
import science.mengxin.spring.web.tutorial.service.LoginService;

import java.util.concurrent.CompletableFuture;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoginActor extends UntypedActor {

    @Autowired
    private LoginService loginService;

    @Override
    public void onReceive(Object message) {
        if (message instanceof LoginMessage) {
            LoginMessage loginMessage = (LoginMessage) message;
            switch (loginMessage.getLoginMessageType()) {
                case INITIAL:
                    Boolean result = loginService.initialLogin(loginMessage);
                    getSender().tell(result, getSelf());
                    break;
                case POLLING:
                    CompletableFuture<String> completableFuture = loginService.pollingLogin(loginMessage);
                    getSender().tell(completableFuture, getSelf());
                    break;
                case SCANNED:
                    Boolean result2 = loginService.finishLogin(loginMessage);
                    getSender().tell(result2, getSelf());
                    break;
            }
        }else {
            unhandled(message);
        }

    }
}
