package rt.sagas.reservation;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.testutils.JmsReceiver;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_CONFIRMED_EVENT_QUEUE;

@Component
public class JmsReservationConfirmedEventReceiver extends JmsReceiver<ReservationConfirmedEvent> {

    @Transactional
    @JmsListener(destination = RESERVATION_CONFIRMED_EVENT_QUEUE)
    @Override
    public void receiveMessage(@Payload ReservationConfirmedEvent reservationConfirmedEvent) {
        addEvent(reservationConfirmedEvent);
    }
}
