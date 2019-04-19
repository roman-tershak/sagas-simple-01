package rt.sagas.orderservice;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static rt.sagas.events.QueueNames.ORDER_QUEUE_NAME;

@Component
public class JmsReceiver {

    private LinkedBlockingQueue<OrderCreatedEvent> events = new LinkedBlockingQueue<>();

    @JmsListener(destination = ORDER_QUEUE_NAME)
    public void receiveMessage(@Payload OrderCreatedEvent orderCreatedEvent) {
        events.add(orderCreatedEvent);
    }

    public OrderCreatedEvent pollEvent() throws InterruptedException {
        return pollEvent(10000L);
    }

    public OrderCreatedEvent pollEvent(long timeout) throws InterruptedException {
        return events.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
