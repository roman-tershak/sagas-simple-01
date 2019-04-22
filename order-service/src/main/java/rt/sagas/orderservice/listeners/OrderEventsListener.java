package rt.sagas.orderservice.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationCompletedEvent;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.entities.OrderStatus;
import rt.sagas.orderservice.repositories.OrderRepository;

import javax.transaction.Transactional;
import java.util.Optional;

import static rt.sagas.events.QueueNames.RESERVATION_QUEUE_NAME;

@Component
public class OrderEventsListener {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @JmsListener(destination = RESERVATION_QUEUE_NAME)
    public void receiveMessage(@Payload ReservationCompletedEvent reservationCompletedEvent) {
        final Long orderId = reservationCompletedEvent.getOrderId();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {

            Order order = optionalOrder.get();

            if (order.getUserId() != reservationCompletedEvent.getUserId()) {
                throw new RuntimeException();
            }
            order.setReservationId(reservationCompletedEvent.getReservationId());
            order.setStatus(OrderStatus.COMPLETE);

            orderRepository.save(order);
        } else {
            throw new RuntimeException();
        }

    }

}
