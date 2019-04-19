package rt.sagas.orderservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.orderservice.entities.Order;

@Component
public class OrderEventsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrderCreatedEvent(Order orderCreated) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderId( orderCreated.getId() );
        orderCreatedEvent.setUserId( orderCreated.getUserId() );
        orderCreatedEvent.setCartNumber( orderCreated.getCartNumber() );

        jmsTemplate.convertAndSend("order.created", orderCreatedEvent);
    }
}
