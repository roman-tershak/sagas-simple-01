package rt.sagas.reservation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.Optional;

import static rt.sagas.events.QueueNames.ORDER_CREATED_EVENT_QUEUE;
import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@Component
public class OrderEventsListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = ORDER_CREATED_EVENT_QUEUE)
    public void receiveMessage(@Payload OrderCreatedEvent orderCreatedEvent) {

        LOGGER.info("Order Created Event received: {}", orderCreatedEvent);

        Long orderId = orderCreatedEvent.getOrderId();
        Long userId = orderCreatedEvent.getUserId();

        Optional<Reservation> reservationsByOrderId = reservationRepository.findByOrderId(orderId);
        if (!reservationsByOrderId.isPresent()) {
            Reservation reservation = reservationFactory.createNewPendingReservationFor(orderId, userId);
            reservationRepository.save(reservation);

            ReservationCreatedEvent reservationCreatedEvent = new ReservationCreatedEvent(
                    reservation.getId(), orderId, userId, orderCreatedEvent.getCartNumber());

            jmsTemplate.convertAndSend(RESERVATION_CREATED_EVENT_QUEUE, reservationCreatedEvent);

            LOGGER.info("Reservation Created Event sent: {}", reservationCreatedEvent);
        } else {
            LOGGER.error("Reservations for Order Id {} has already been created", orderId);
        }
    }
}
