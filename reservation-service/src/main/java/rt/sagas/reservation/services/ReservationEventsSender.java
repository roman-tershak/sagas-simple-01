package rt.sagas.reservation.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import rt.sagas.events.QueueNames;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.events.ReservationCreatedEvent;

import javax.transaction.Transactional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;
import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@Service
public class ReservationEventsSender {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional(REQUIRES_NEW)
    public void sendReservationCreatedEvent(String reservationId, Long orderId, Long userId, String cartNumber) {
        ReservationCreatedEvent reservationCreatedEvent = new ReservationCreatedEvent(
                reservationId, orderId, userId, cartNumber);

        jmsTemplate.convertAndSend(RESERVATION_CREATED_EVENT_QUEUE, reservationCreatedEvent);

        LOGGER.info("Reservation Created Event sent: {}", reservationCreatedEvent);
    }

    @Transactional(REQUIRES_NEW)
    public void sendReservationConfirmedEvent(String reservationId, Long orderId, Long userId) {
        ReservationConfirmedEvent reservationConfirmedEvent = new ReservationConfirmedEvent(
                reservationId, orderId, userId);

        jmsTemplate.convertAndSend(QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE, reservationConfirmedEvent);

        LOGGER.info("Reservation Confirmed Event sent: {}", reservationConfirmedEvent);
    }
}
