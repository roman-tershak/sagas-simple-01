package rt.sagas.reservation;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationErrorEvent;
import rt.sagas.testutils.JmsReceiver;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_ERROR_EVENT_QUEUE;

@Component
public class JmsReservationErrorEventReceiver extends JmsReceiver<ReservationErrorEvent> {

    @Transactional
    @JmsListener(destination = RESERVATION_ERROR_EVENT_QUEUE)
    @Override
    public void receiveMessage(@Payload ReservationErrorEvent reservationErrorEvent) {
        addEvent(reservationErrorEvent);
    }
}
