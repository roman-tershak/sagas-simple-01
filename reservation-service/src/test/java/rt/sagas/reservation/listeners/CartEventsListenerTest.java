package rt.sagas.reservation.listeners;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.CartAuthorizedEvent;
import rt.sagas.events.QueueNames;
import rt.sagas.events.ReservationConfirmedEvent;
import rt.sagas.events.ReservationErrorEvent;
import rt.sagas.reservation.JmsReservationConfirmedEventReceiver;
import rt.sagas.reservation.JmsReservationErrorEventReceiver;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.entities.ReservationFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rt.sagas.reservation.entities.ReservationStatus.CONFIRMED;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartEventsListenerTest extends AbstractListenerTest {

    public static final Long ORDER_ID = 15L;
    public static final Long USER_ID = 678L;
    public static final String CART_NUMBER = "1234543211234567";

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsReservationConfirmedEventReceiver reservationConfirmedEventReceiver;
    @Autowired
    private JmsReservationErrorEventReceiver reservationErrorEventReceiver;

    @After
    public void tearDown() {
        super.tearDown();
        reservationConfirmedEventReceiver.clear();
    }

    @Test
    public void testReservationGetsCompletedWhenCartApprovedEventIsReceived() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(
                pendingReservation.getId(),
                CART_NUMBER, ORDER_ID, USER_ID);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        List<Reservation> reservations = waitAndGetReservationsByOrderIdFromDb(ORDER_ID, 5000L);
        assertThat(reservations.size(), is(1));

        Reservation reservation = reservations.get(0);
        assertThat(reservation.getId(), is(pendingReservation.getId()));
        assertThat(reservation.getOrderId(), is(ORDER_ID));
        assertThat(reservation.getUserId(), is(USER_ID));
        assertThat(reservation.getStatus(), is(CONFIRMED));
    }

    @Test
    public void testReservationCompletedEventIsSentWhenCartAuthorizedEventIsReceived() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(
                pendingReservation.getId(),
                CART_NUMBER, ORDER_ID, USER_ID);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        ReservationConfirmedEvent reservationConfirmedEvent = reservationConfirmedEventReceiver.pollEvent();
        assertThat(reservationConfirmedEvent, is(notNullValue()));
        assertThat(reservationConfirmedEvent.getReservationId(), is(pendingReservation.getId()));
        assertThat(reservationConfirmedEvent.getOrderId(), is(ORDER_ID));
        assertThat(reservationConfirmedEvent.getUserId(), is(USER_ID));
    }

    @Test
    public void testReservationErrorEventIsSentWhenNoPendingReservationIsFound() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(
                "not-existing-reservation",
                CART_NUMBER, ORDER_ID, USER_ID);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        ReservationConfirmedEvent reservationConfirmedEvent = reservationConfirmedEventReceiver.pollEvent(5000L);
        assertThat(reservationConfirmedEvent, is(nullValue()));

        ReservationErrorEvent reservationErrorEvent = reservationErrorEventReceiver.pollEvent(5000L);
        assertThat(reservationErrorEvent, is(notNullValue()));
        assertThat(reservationErrorEvent.getReservationId(), is("not-existing-reservation"));
        assertThat(reservationErrorEvent.getOrderId(), is(ORDER_ID));
        assertThat(reservationErrorEvent.getUserId(), is(USER_ID));
        assertThat(reservationErrorEvent.getCartNumber(), is(CART_NUMBER));
        assertThat(reservationErrorEvent.getMessage(), containsString("The Reservation 'not-existing-reservation' does not exist"));
    }
}
