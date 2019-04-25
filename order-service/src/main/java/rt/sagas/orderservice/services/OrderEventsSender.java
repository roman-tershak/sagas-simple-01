package rt.sagas.orderservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.orderservice.entities.Order;

import static rt.sagas.events.QueueNames.ORDER_QUEUE_NAME;

@Component
public class OrderEventsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrderCreatedEvent(Order orderCreated) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderCreated.getId(),
                orderCreated.getUserId(),
                orderCreated.getCartNumber());

        jmsTemplate.convertAndSend(ORDER_QUEUE_NAME, orderCreatedEvent);
    }
}
