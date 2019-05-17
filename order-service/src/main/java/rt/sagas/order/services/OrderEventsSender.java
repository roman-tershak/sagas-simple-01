package rt.sagas.order.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.order.entities.Order;

import static rt.sagas.events.QueueNames.ORDER_CREATED_EVENT_QUEUE;

@Component
public class OrderEventsSender {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrderCreatedEvent(Order orderCreated) {

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                orderCreated.getId(),
                orderCreated.getUserId(),
                orderCreated.getCartNumber());

        jmsTemplate.convertAndSend(ORDER_CREATED_EVENT_QUEUE, orderCreatedEvent);

        LOGGER.info("Order Created Event sent: {}", orderCreatedEvent);
    }
}
