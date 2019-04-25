package rt.sagas.reservation.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.events.ReservationCreatedEvent;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.entities.ReservationFactory;
import rt.sagas.reservation.repositories.ReservationRepository;

import javax.transaction.Transactional;
import java.util.List;

import static rt.sagas.events.QueueNames.ORDER_QUEUE_NAME;
import static rt.sagas.events.QueueNames.RESERVATION_QUEUE_NAME;

@Component
public class OrderEventsListener {

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = ORDER_QUEUE_NAME)
    public void receiveMessage(@Payload OrderCreatedEvent orderCreatedEvent) {
        Long orderId = orderCreatedEvent.getOrderId();
        Long userId = orderCreatedEvent.getUserId();

        List<Reservation> reservationsByOrderId = reservationRepository.findAllByOrderId(orderId);
        if (reservationsByOrderId.size() == 0) {
            Reservation reservation = reservationFactory.createNewPendingReservationFor(orderId, userId);
            reservationRepository.save(reservation);

            ReservationCreatedEvent reservationCreatedEvent = new ReservationCreatedEvent(
                    reservation.getId(), orderId, userId, orderCreatedEvent.getCartNumber());

            jmsTemplate.convertAndSend(RESERVATION_QUEUE_NAME, reservationCreatedEvent);
        } else {
            // TODO ignore for now
        }
    }
}
