package rt.sagas.reservation.listeners;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.OrderCreatedEvent;
import rt.sagas.events.QueueNames;
import rt.sagas.events.ReservationCreatedEvent;
import rt.sagas.reservation.JmsReservationCreatedEventReceiver;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.entities.ReservationFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rt.sagas.reservation.entities.ReservationStatus.PENDING;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderEventsListenerTest extends AbstractListenerTest {

    private static final Long ORDER_ID = 123333L;
    private static final Long USER_ID = 111111L;
    private static final String CART_NUMBER = "1111567890123456";

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsReservationCreatedEventReceiver reservationCreatedEventReceiver;

    @After
    public void tearDown() {
        super.tearDown();
        reservationCreatedEventReceiver.clear();
    }

    @Test
    public void testPendingReservationIsCreatedAndReservationCreatedEventIsSentOnOrderCreatedEvent() throws Exception {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(ORDER_ID, USER_ID, CART_NUMBER);
        jmsTemplate.convertAndSend(QueueNames.ORDER_CREATED_EVENT_QUEUE, orderCreatedEvent);

        Reservation reservation = waitAndGetReservationsByOrderIdAndStatusFromDb(
                ORDER_ID, PENDING, 10000L);
        assertThat(reservation.getOrderId(), is(ORDER_ID));
        assertThat(reservation.getUserId(), is(USER_ID));

        ReservationCreatedEvent reservationCreatedEvent = reservationCreatedEventReceiver.pollEvent(5000L);
        assertThat(reservationCreatedEvent, is(notNullValue()));
        assertThat(reservationCreatedEvent.getReservationId(), is(reservation.getId()));
        assertThat(reservationCreatedEvent.getOrderId(), is(ORDER_ID));
        assertThat(reservationCreatedEvent.getUserId(), is(USER_ID));
        assertThat(reservationCreatedEvent.getCartNumber(), is(CART_NUMBER));
    }

    @Test
    public void testPendingReservationIsNotCreatedIfItAlreadyHasBeenCreatedForAGivenOrderId() throws Exception {
        Reservation pendingReservation = reservationFactory.createNewPendingReservationFor(ORDER_ID, USER_ID);
        reservationRepository.save(pendingReservation);

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(ORDER_ID, USER_ID, CART_NUMBER);
        jmsTemplate.convertAndSend(QueueNames.ORDER_CREATED_EVENT_QUEUE, orderCreatedEvent);

        ReservationCreatedEvent reservationCreatedEvent = reservationCreatedEventReceiver.pollEvent(5000L);
        assertThat(reservationCreatedEvent, is(nullValue()));

        List<Reservation> reservationsFromDb = reservationRepository.findAllByOrderId(ORDER_ID);
        assertThat(reservationsFromDb.size(), is(1));
        Reservation reservationFromDb = reservationsFromDb.get(0);
        assertThat(reservationFromDb.getId(), is(pendingReservation.getId()));
        assertThat(reservationFromDb.getOrderId(), is(pendingReservation.getOrderId()));
        assertThat(reservationFromDb.getUserId(), is(pendingReservation.getUserId()));
        assertThat(reservationFromDb.getStatus(), is(PENDING));
    }


}
