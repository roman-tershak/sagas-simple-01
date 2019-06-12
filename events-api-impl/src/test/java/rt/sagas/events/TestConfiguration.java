package rt.sagas.events;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import rt.sagas.events.services.EventService;

@SpringBootApplication
public class TestConfiguration {

    public static final String TEST_DESTINATION = "testDestination";

    @Bean
    public EventService testEventService() {
        return new EventService(TEST_DESTINATION);
    }
}
