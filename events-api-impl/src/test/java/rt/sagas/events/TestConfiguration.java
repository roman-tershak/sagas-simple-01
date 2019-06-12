package rt.sagas.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import rt.sagas.events.services.EventService;

@org.springframework.boot.test.context.TestConfiguration
@ComponentScan(basePackages = "rt.sagas.events")
@EnableJms
@EnableJpaRepositories
public class TestConfiguration {

    public static final String TEST_DESTINATION = "testDestination";

    @Bean
    public EventService testEventService() {
        return new EventService(QueueNames.CART_AUTHORIZED_EVENT_QUEUE);
    }
}
