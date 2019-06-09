package rt.sagas.reservation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.CartAuthorizedEvent;
import rt.sagas.reservation.services.ReservationService;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.CART_AUTHORIZED_EVENT_QUEUE;

@Component
public class CartEvensListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ReservationService reservationService;

    @Transactional
    @JmsListener(destination = CART_AUTHORIZED_EVENT_QUEUE)
    public void receiveMessage(@Payload CartAuthorizedEvent cartAuthorizedEvent) {
        LOGGER.info("Cart Authorized Event received: {}", cartAuthorizedEvent);

        reservationService.confirmReservation(
                cartAuthorizedEvent.getReservationId(),
                cartAuthorizedEvent.getOrderId(),
                cartAuthorizedEvent.getUserId(),
                cartAuthorizedEvent.getCartNumber());

        LOGGER.info("About to complete Cart Authorized Event handling: {}", cartAuthorizedEvent);
    }

}
