package science.mengxin.spring.web.tutorial.web.rest;

import akka.actor.ActorRef;

import akka.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import science.mengxin.spring.web.tutorial.config.AkkaConfig;
import science.mengxin.spring.web.tutorial.domain.LoginMessage;
import science.mengxin.spring.web.tutorial.domain.LoginMessageType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

@RestController
@RequestMapping("akka")
public class AkkaResource {

    private Logger log = LoggerFactory.getLogger(AkkaResource.class);

    private ActorRef loginActor;


    public AkkaResource(@Qualifier(AkkaConfig.LOGIN_ACTOR) ActorRef loginActor) {
        this.loginActor = loginActor;
    }


    private String defaultUserName = "alice";


    @GetMapping("/login")
    public ResponseEntity<String> initialLogin() {
        log.info("initial login");
        FiniteDuration duration = FiniteDuration.create(5, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> result = ask(loginActor, new LoginMessage(defaultUserName, LoginMessageType.INITIAL, "", ""), timeout);
        Object str = "";
        try {
            str = Await.result(result, duration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(str.toString(), HttpStatus.OK);
    }


    @GetMapping("polling")
    public DeferredResult<String> polling() {
        DeferredResult<String> deferred = new DeferredResult<>();

        FiniteDuration duration = FiniteDuration.create(5, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> result = ask(loginActor, new LoginMessage(defaultUserName, LoginMessageType.POLLING, "", ""), timeout);


        CompletableFuture<String> future = null;
        try {
            future = (CompletableFuture<String>) Await.result(result, duration);
            log.info("start polling");
            future.whenComplete((complete, error) -> {
                log.info("get the scan result and finish polling: {}", complete);
                if (error != null) {
                    deferred.setErrorResult(error);
                } else {
                    deferred.setResult(complete);
                }
            });
            log.info("async request finish");
            return deferred;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/scan")
    public ResponseEntity<String> scan() {
        FiniteDuration duration = FiniteDuration.create(5, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        log.info("start to scan");
        ask(loginActor, new LoginMessage(defaultUserName, LoginMessageType.SCANNED, "finish", "test"), timeout);
        log.info("finish scan");
        Object str = "Scan Finish";
        return new ResponseEntity<>(str.toString(), HttpStatus.OK);
    }
}
