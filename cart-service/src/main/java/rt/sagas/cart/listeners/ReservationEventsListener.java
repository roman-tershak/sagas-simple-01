package rt.sagas.cart.listeners;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.cart.entities.Transaction;
import rt.sagas.cart.entities.TransactionStatus;
import rt.sagas.cart.repositories.TransactionRepository;
import rt.sagas.events.CartAuthorizedEvent;
import rt.sagas.events.QueueNames;
import rt.sagas.events.ReservationCreatedEvent;

import javax.transaction.Transactional;

import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@Component
public class ReservationEventsListener {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = RESERVATION_CREATED_EVENT_QUEUE)
    public void receiveMessage(@Payload ReservationCreatedEvent reservationCreatedEvent) {
        String reservationId = reservationCreatedEvent.getReservationId();
        Long orderId = reservationCreatedEvent.getOrderId();
        Long userId = reservationCreatedEvent.getUserId();
        String cartNumber = reservationCreatedEvent.getCartNumber();

        Transaction transaction = new Transaction(cartNumber, orderId, userId, TransactionStatus.AUTHORIZED);
        transactionRepository.save(transaction);

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(reservationId, cartNumber, orderId, userId);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);
    }
}
