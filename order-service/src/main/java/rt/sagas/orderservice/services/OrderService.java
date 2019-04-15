package rt.sagas.orderservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.repositories.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {

        return orderRepository.save(order);
    }
}
