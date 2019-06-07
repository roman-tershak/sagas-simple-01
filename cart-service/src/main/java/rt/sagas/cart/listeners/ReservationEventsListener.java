package rt.sagas.cart.listeners;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.cart.services.TransactionService;
import rt.sagas.events.ReservationCreatedEvent;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@Component
public class ReservationEventsListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private TransactionService transactionService;

    @Transactional
    @JmsListener(destination = RESERVATION_CREATED_EVENT_QUEUE)
    public void receiveMessage(@Payload ReservationCreatedEvent reservationCreatedEvent) {
        LOGGER.info("Reservation Created Event received: {}", reservationCreatedEvent);

        String reservationId = reservationCreatedEvent.getReservationId();
        Long orderId = reservationCreatedEvent.getOrderId();
        Long userId = reservationCreatedEvent.getUserId();
        String cartNumber = reservationCreatedEvent.getCartNumber();

        transactionService.authorizeTransaction(reservationId, orderId, userId, cartNumber);

        LOGGER.info("About to complete Reservation Created Event handling: {}", reservationCreatedEvent);
    }
}
