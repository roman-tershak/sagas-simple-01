package rt.sagas.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import rt.sagas.cart.services.EventService;

import static rt.sagas.events.QueueNames.CART_AUTHORIZED_EVENT_QUEUE;

@SpringBootApplication
@EnableJpaRepositories
@EnableJms
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public EventService transactionEventService() {
        return new EventService(CART_AUTHORIZED_EVENT_QUEUE);
    }
}
