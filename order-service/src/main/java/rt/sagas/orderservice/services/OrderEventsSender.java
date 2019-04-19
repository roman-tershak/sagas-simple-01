package rt.sagas.orderservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.orderservice.events.OrderEvent;

@Component
public class OrderEventsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendOrderEvent(OrderEvent orderEvent) {

        jmsTemplate.convertAndSend("order.created", orderEvent);
    }
}
