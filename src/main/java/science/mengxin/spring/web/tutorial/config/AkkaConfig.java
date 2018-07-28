package science.mengxin.spring.web.tutorial.config;

import akka.actor.ActorRef;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import science.mengxin.spring.akka.AkkaConfiguration;
import science.mengxin.spring.akka.SpringExtension;

@Configuration
@ComponentScan
public class AkkaConfig extends AkkaConfiguration {

    public static final String LOGIN_ACTOR = "login-actor";

    public AkkaConfig(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Bean(name = LOGIN_ACTOR)
    @DependsOn({"default-actorSystem"})
    public ActorRef loginActor() {
        return this.getActorSystem()
                .actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER
                        .get(this.getActorSystem())
                        .props("loginActor"), LOGIN_ACTOR);
    }
}
