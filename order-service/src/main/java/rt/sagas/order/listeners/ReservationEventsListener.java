package rt.sagas.order.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.order.services.OrderService;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE;

@Component
public class ReservationEventsListener {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private OrderService orderService;

    @Transactional
    @JmsListener(destination = RESERVATION_CONFIRMED_EVENT_QUEUE)
    public void receiveMessage(@Payload ReservationConfirmedEvent reservationConfirmedEvent) {
        try {
            LOGGER.info("Reservation Confirmed Event received: {}", reservationConfirmedEvent);

            orderService.completeOrder(
                    reservationConfirmedEvent.getReservationId(),
                    reservationConfirmedEvent.getOrderId());

            LOGGER.info("About to complete Reservation Confirmed Event handling: {}", reservationConfirmedEvent);
        } catch (Exception e) {
            LOGGER.error("An exception occurred in Reservation Confirmed Event handling: {}, {}", reservationConfirmedEvent, e);
            throw e;
        }
    }
}
