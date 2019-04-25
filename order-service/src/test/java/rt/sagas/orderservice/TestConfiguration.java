package rt.sagas.orderservice;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.repositories.OrderRepository;
import rt.sagas.orderservice.services.OrderEventsSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Configuration
public class TestConfiguration {

    @Primary
    @Bean
    public OrderEventsSender orderEventsSenderSpy(OrderEventsSender orderEventsSender) {
        OrderEventsSender orderEventsSenderSpy = Mockito.spy(orderEventsSender);

        doAnswer(invocation -> {
            return invocation.callRealMethod();
        }).when(orderEventsSenderSpy).sendOrderCreatedEvent(any(Order.class));

        return orderEventsSenderSpy;
    }

    @Primary
    @Bean
    public OrderRepository orderRepositorySpy(OrderRepository orderRepository) {
        return new OrderRepositorySpy(orderRepository);
    }
}
