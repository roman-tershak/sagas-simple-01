package rt.sagas.orderservice;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.testutils.JmsReceiver;

import static rt.sagas.events.QueueNames.ORDER_QUEUE_NAME;

@Component
public class JmsOrderEventsReceiver extends JmsReceiver<OrderCreatedEvent> {

    @JmsListener(destination = ORDER_QUEUE_NAME)
    @Override
    public void receiveMessage(@Payload OrderCreatedEvent orderCreatedEvent) {
        addEvent(orderCreatedEvent);
    }
}
