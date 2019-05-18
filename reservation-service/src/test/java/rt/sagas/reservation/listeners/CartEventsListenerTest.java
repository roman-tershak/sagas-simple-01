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
import rt.sagas.reservation.ReservationRepositorySpy;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.entities.ReservationFactory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rt.sagas.reservation.entities.ReservationStatus.CONFIRMED;
import static rt.sagas.reservation.entities.ReservationStatus.PENDING;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartEventsListenerTest extends AbstractListenerTest {

    public static final Long ORDER_ID = 15L;
    public static final Long USER_ID = 11112L;
    public static final String CART_NUMBER = "2222543211234567";

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private ReservationRepositorySpy reservationRepositorySpy;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsReservationConfirmedEventReceiver reservationConfirmedEventReceiver;
    @Autowired
    private JmsReservationErrorEventReceiver reservationErrorEventReceiver;

    @After
    public void setUp() {
        reservationRepositorySpy.setThrowExceptionInSave(false);
    }

    @After
    public void tearDown() {
        System.out.println("tear down");
        reservationRepositorySpy.setThrowExceptionInSave(false);
        super.tearDown();
        reservationConfirmedEventReceiver.clear();
        reservationErrorEventReceiver.clear();
    }

    @Test
    public void testReservationGetsCompletedAndReservationCompletedEventIsSentWhenCartApprovedEventIsReceived() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);
        String reservationId = pendingReservation.getId();

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(
                reservationId,
                CART_NUMBER, ORDER_ID, USER_ID);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        Reservation reservation = waitAndGetReservationsByIdAndStatusFromDb(
                reservationId, CONFIRMED, 5000L);
        assertThat(reservation, is(notNullValue()));
        assertThat(reservation.getOrderId(), is(ORDER_ID));
        assertThat(reservation.getUserId(), is(USER_ID));

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

    @Test
    public void testReservationConfirmedEventIsNotSentWhenExceptionOccurs() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);
        String reservationId = pendingReservation.getId();

        reservationRepositorySpy.setThrowExceptionInSave(true);

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(
                reservationId,
                CART_NUMBER, ORDER_ID, USER_ID);
        jmsTemplate.convertAndSend(QueueNames.CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        assertThat(reservationConfirmedEventReceiver.pollEvent(5000L), is(nullValue()));
        assertThat(reservationErrorEventReceiver.pollEvent(1000L), is(nullValue()));

        Reservation reservation = waitAndGetReservationsByIdAndStatusFromDb(
                reservationId, PENDING, 0);
        assertThat(reservation, is(notNullValue()));
        assertThat(reservation.getOrderId(), is(ORDER_ID));
        assertThat(reservation.getUserId(), is(USER_ID));
    }
}
