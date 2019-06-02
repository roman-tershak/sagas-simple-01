package rt.sagas.cart.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.ReservationCreatedEvent;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@Component
public class JmsReservationCreatedEventSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public void send(ReservationCreatedEvent reservationCreatedEvent) {
        jmsTemplate.convertAndSend(RESERVATION_CREATED_EVENT_QUEUE, reservationCreatedEvent);
    }
}
