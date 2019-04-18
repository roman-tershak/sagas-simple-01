package rt.sagas.orderservice;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.orderservice.events.OrderCreated;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class JmsReceiver {

    private LinkedBlockingQueue<OrderCreated> events = new LinkedBlockingQueue<>();

    @JmsListener(destination = "order.created")
    public void receiveMessage(@Payload OrderCreated orderCreated) {
        events.add(orderCreated);
    }

    public OrderCreated pollEvent() throws InterruptedException {
        return pollEvent(10000L);
    }

    public OrderCreated pollEvent(long timeout) throws InterruptedException {
        return events.poll(timeout, TimeUnit.MILLISECONDS);
    }
}
