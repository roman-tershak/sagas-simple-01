package rt.sagas.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.order.entities.Order;

import static rt.sagas.events.QueueNames.ORDER_CREATED_EVENT_QUEUE;

@Component
public class OrderEventsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrderCreatedEvent(Order orderCreated) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderCreated.getId(),
                orderCreated.getUserId(),
                orderCreated.getCartNumber());

        jmsTemplate.convertAndSend(ORDER_CREATED_EVENT_QUEUE, orderCreatedEvent);
    }
}
