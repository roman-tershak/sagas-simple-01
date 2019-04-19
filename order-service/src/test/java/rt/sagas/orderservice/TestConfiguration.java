package rt.sagas.orderservice;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.services.OrderEventsSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Configuration
public class TestConfiguration {

    @Primary
    @Bean
    public OrderEventsSender getOrderEventsSenderSpy(OrderEventsSender orderEventsSender) {
        OrderEventsSender orderEventsSenderSpy = Mockito.spy(orderEventsSender);

        doAnswer(invocationOnMock -> {
            return invocationOnMock.callRealMethod();
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));

        return orderEventsSenderSpy;
    }
}
