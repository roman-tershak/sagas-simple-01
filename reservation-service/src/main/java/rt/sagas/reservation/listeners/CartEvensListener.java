package rt.sagas.reservation.listeners;

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
import java.util.Objects;
import java.util.Optional;

import static rt.sagas.events.QueueNames.CART_AUTHORIZED_EVENT_QUEUE;
import static rt.sagas.reservation.entities.ReservationStatus.CONFIRMED;

@Component
public class CartEvensListener {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = CART_AUTHORIZED_EVENT_QUEUE)
    public void receiveMessage(@Payload CartAuthorizedEvent cartAuthorizedEvent) {
        String reservationId = cartAuthorizedEvent.getReservationId();
        Long orderId = cartAuthorizedEvent.getOrderId();
        Long userId = cartAuthorizedEvent.getUserId();

        Optional<Reservation> optional = reservationRepository.findById(reservationId);
        if (optional.isPresent()) {
            Reservation reservation = optional.get();
            Long reservationOrderId = reservation.getOrderId();
            Long reservationUserId = reservation.getUserId();

            if (Objects.equals(reservationOrderId, orderId) &&
                    Objects.equals(reservationUserId, userId)) {

                reservation.setStatus(CONFIRMED);
                reservationRepository.save(reservation);

                ReservationConfirmedEvent reservationConfirmedEvent = new ReservationConfirmedEvent(
                        reservationId, orderId, userId);

                jmsTemplate.convertAndSend(QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE, reservationConfirmedEvent);
            } else {
                ReservationErrorEvent reservationErrorEvent = new ReservationErrorEvent(
                        reservationId, reservationOrderId, reservationUserId, cartAuthorizedEvent.getCartNumber(),
                        "The Reservation attributes (" + orderId + ", " + userId + ") do not match");

                jmsTemplate.convertAndSend(QueueNames.RESERVATION_ERROR_EVENT_QUEUE, reservationErrorEvent);
            }

        } else {
            ReservationErrorEvent reservationErrorEvent = new ReservationErrorEvent(
                    reservationId, orderId, userId, cartAuthorizedEvent.getCartNumber(),
                    "The Reservation '" + reservationId + "' does not exist");

            jmsTemplate.convertAndSend(QueueNames.RESERVATION_ERROR_EVENT_QUEUE, reservationErrorEvent);
        }
    }
}
