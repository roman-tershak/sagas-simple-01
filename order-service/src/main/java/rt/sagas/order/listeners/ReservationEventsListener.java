package rt.sagas.order.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.order.entities.Order;
import rt.sagas.order.entities.OrderStatus;
import rt.sagas.order.repositories.OrderRepository;

import javax.transaction.Transactional;
import java.util.Optional;

import static rt.sagas.events.QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE;

@Component
public class ReservationEventsListener {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @JmsListener(destination = RESERVATION_CONFIRMED_EVENT_QUEUE)
    public void receiveMessage(@Payload ReservationConfirmedEvent reservationConfirmedEvent) {
        final Long orderId = reservationConfirmedEvent.getOrderId();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {

            Order order = optionalOrder.get();

            if (order.getUserId() == reservationConfirmedEvent.getUserId()) {
                order.setReservationId(reservationConfirmedEvent.getReservationId());
                order.setStatus(OrderStatus.COMPLETE);

                orderRepository.save(order);
            }
        }
    }

}
