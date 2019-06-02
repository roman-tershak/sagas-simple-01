package rt.sagas.cart.listeners;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.cart.entities.Transaction;
import rt.sagas.cart.repositories.TransactionRepository;
import rt.sagas.events.CartAuthorizedEvent;
import rt.sagas.events.ReservationCreatedEvent;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static rt.sagas.cart.entities.TransactionStatus.AUTHORIZED;
import static rt.sagas.events.QueueNames.RESERVATION_CREATED_EVENT_QUEUE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationEventsListenerTest {

    private static final String RESERVATION_ID = "123456-1234-654321";
    private static final long ORDER_ID = 12345L;
    private static final long USER_ID = 123L;
    private static final String CART_NUMBER = "1234567890123456";

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private JmsCartAuthorizedEventReceiver cartAuthorizedEventReceiver;
    @Autowired
    private JmsReservationCreatedEventSender jmsSender;

    @Test
    public void testTransactionIsCreatedAndCartAuthorizedEventIsSentOnReservationCreatedEvent() throws Exception {
        ReservationCreatedEvent reservationCreatedEvent = new ReservationCreatedEvent(
                RESERVATION_ID, ORDER_ID, USER_ID, CART_NUMBER);

        jmsSender.send(reservationCreatedEvent);

        CartAuthorizedEvent cartAuthorizedEvent = cartAuthorizedEventReceiver.pollEvent(5000L);
        assertThat(cartAuthorizedEvent, is(notNullValue()));
        assertThat(cartAuthorizedEvent.getReservationId(), is(RESERVATION_ID));
        assertThat(cartAuthorizedEvent.getOrderId(), is(ORDER_ID));
        assertThat(cartAuthorizedEvent.getUserId(), is(USER_ID));
        assertThat(cartAuthorizedEvent.getCartNumber(), is(CART_NUMBER));

        Optional<Transaction> optionalTransaction = transactionRepository.findByOrderId(ORDER_ID);
        assertThat(optionalTransaction.isPresent(), is(true));
        Transaction transaction = optionalTransaction.get();
        assertThat(transaction.getOrderId(), is(ORDER_ID));
        assertThat(transaction.getUserId(), is(USER_ID));
        assertThat(transaction.getCartNumber(), is(CART_NUMBER));
        assertThat(transaction.getStatus(), is(AUTHORIZED));
    }
}
