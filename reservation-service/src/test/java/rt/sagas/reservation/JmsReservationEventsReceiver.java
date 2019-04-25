package rt.sagas.reservation;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationCreatedEvent;
import rt.sagas.testutils.JmsReceiver;

import static rt.sagas.events.QueueNames.RESERVATION_QUEUE_NAME;

@Component
public class JmsReservationEventsReceiver extends JmsReceiver<ReservationCreatedEvent> {

    @JmsListener(destination = RESERVATION_QUEUE_NAME)
    @Override
    public void receiveMessage(@Payload ReservationCreatedEvent reservationCreatedEvent) {
        addEvent(reservationCreatedEvent);
    }
}
