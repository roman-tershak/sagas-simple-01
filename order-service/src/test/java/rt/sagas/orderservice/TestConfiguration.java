package rt.sagas.orderservice;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import rt.sagas.orderservice.services.OrderEventsSender;

//@Profile("OrderService-test")
@Configuration
public class TestConfiguration {

    @Primary
    @Bean
    public OrderEventsSender getOrderEventsSenderSpy(OrderEventsSender orderEventsSender) {
        return Mockito.spy(orderEventsSender);
    }
}
