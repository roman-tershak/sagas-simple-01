package rt.sagas.reservation;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import rt.sagas.reservation.repositories.ReservationRepository;

@Configuration
@ComponentScan(basePackages = "rt.sagas")
public class TestConfiguration {

    @Primary
    @Bean
    public ReservationRepositorySpy reservationRepositorySpy(ReservationRepository reservationRepository) {
        return new ReservationRepositorySpy(reservationRepository);
    }

    @Bean
    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    public InitializingBean jmsConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        return configureRedeliveryPolicy(connectionFactory);
    }

    private InitializingBean configureRedeliveryPolicy(ActiveMQConnectionFactory connectionFactory) {
        return () ->
        {
            connectionFactory.setRetryInterval(1000000L);
        };
    }
}
