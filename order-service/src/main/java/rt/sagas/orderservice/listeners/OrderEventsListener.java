package rt.sagas.orderservice.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rt.sagas.orderservice.services.OrderService;

@Component
public class OrderEventsListener {

    @Autowired
    private OrderService orderService;
//
//    @JmsListener(destination = "order.created")
//    public void receiveMessage(@Payload OrderCreated orderCreated) {
//        events.add(orderCreated);
//    }

}
