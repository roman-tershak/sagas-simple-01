package rt.sagas.reservation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.CartAuthorizedEvent;
import rt.sagas.events.QueueNames;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.events.ReservationErrorEvent;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.repositories.ReservationRepository;

import javax.transaction.Transactional;
import java.util.Optional;

import static rt.sagas.events.QueueNames.CART_AUTHORIZED_EVENT_QUEUE;
import static rt.sagas.reservation.entities.ReservationStatus.CONFIRMED;

@Component
public class CartEvensListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = CART_AUTHORIZED_EVENT_QUEUE)
    public void receiveMessage(@Payload CartAuthorizedEvent cartAuthorizedEvent) {
        LOGGER.info("Cart Authorized Event received: {}", cartAuthorizedEvent);

        String reservationId = cartAuthorizedEvent.getReservationId();
        Optional<Reservation> optional = reservationRepository.findById(reservationId);

        if (optional.isPresent()) {
            Reservation reservation = optional.get();

            reservation.setStatus(CONFIRMED);
            reservationRepository.save(reservation);

            sendReservationConfirmedEvent(reservation);
        } else {
            handleReservationMissedError(cartAuthorizedEvent);
        }
    }

    private void sendReservationConfirmedEvent(Reservation reservation) {
        ReservationConfirmedEvent reservationConfirmedEvent = new ReservationConfirmedEvent(
                reservation.getId(), reservation.getOrderId(), reservation.getUserId());

        jmsTemplate.convertAndSend(QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE, reservationConfirmedEvent);

        LOGGER.info("Reservation Confirmed Event sent: {}", reservationConfirmedEvent);
    }

    private void handleReservationMissedError(CartAuthorizedEvent cartAuthorizedEvent) {
        String reservationId = cartAuthorizedEvent.getReservationId();

        ReservationErrorEvent reservationErrorEvent = new ReservationErrorEvent(
                reservationId,
                cartAuthorizedEvent.getOrderId(),
                cartAuthorizedEvent.getUserId(),
                cartAuthorizedEvent.getCartNumber(),
                "The Reservation '" + reservationId + "' does not exist");
        jmsTemplate.convertAndSend(QueueNames.RESERVATION_ERROR_EVENT_QUEUE, reservationErrorEvent);

        LOGGER.error("Reservation Error Event sent: {}", reservationErrorEvent);
    }
}
