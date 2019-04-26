package rt.sagas.order.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.order.entities.Order;
import rt.sagas.order.repositories.OrderRepository;

import javax.transaction.Transactional;

import static javax.transaction.Transactional.TxType.REQUIRED;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventsSender eventsSender;

    @Transactional(REQUIRED)
    public Order createOrder(Order order) {

        Order orderSaved = orderRepository.save(order);

        eventsSender.sendOrderCreatedEvent(orderSaved);

        return orderSaved;
    }
}
