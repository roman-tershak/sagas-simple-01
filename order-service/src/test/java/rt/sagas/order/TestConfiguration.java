package rt.sagas.order;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import rt.sagas.order.entities.Order;
import rt.sagas.order.repositories.OrderRepository;
import rt.sagas.order.services.OrderEventsSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Configuration
@EnableTransactionManagement
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
