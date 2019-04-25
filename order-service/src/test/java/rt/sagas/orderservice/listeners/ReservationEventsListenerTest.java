package rt.sagas.orderservice.listeners;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.ReservationCompletedEvent;
import rt.sagas.orderservice.OrderRepositorySpy;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.entities.OrderStatus;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static rt.sagas.events.QueueNames.RESERVATION_QUEUE_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationEventsListenerTest {

    private static final long USER_ID = 12L;
    private static final String RESERVATION_ID = "ABCDEF-1234-8765-UVWXYZ-12";

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private OrderRepositorySpy orderRepositorySpy;

    private long orderId;

    @Before
    public void setUp() {
        orderRepositorySpy.setThrowExceptionInSave(false);

        Order order = new Order(USER_ID, "1234567890123456");
        final Order orderSaved = orderRepositorySpy.save(order);
        orderId = orderSaved.getId();
    }

    @After
    public void tearDown() {
        orderRepositorySpy.setThrowExceptionInSave(false);
    }

    @Test
    public void testOrderBecomesCompletedOnReservationFinalizedEvent() throws Exception {
        ReservationCompletedEvent reservationCompletedEvent = new ReservationCompletedEvent(
                RESERVATION_ID, orderId, USER_ID);

        jmsTemplate.convertAndSend(RESERVATION_QUEUE_NAME, reservationCompletedEvent);

        Order order = waitTillCompletedAndGetOrderFromDb(5000L);

        assertThat(order.getStatus(), is(OrderStatus.COMPLETE));
        assertThat(order.getUserId(), is(USER_ID));
        assertThat(order.getReservationId(), is(RESERVATION_ID));
    }

    @Test
    public void testOrderDoesNotBecomeCompletedWhenHandlingOfReservationFinalizedEventFails() throws Exception {
        ReservationCompletedEvent reservationCompletedEvent = new ReservationCompletedEvent(
                RESERVATION_ID, orderId, 999L);

        jmsTemplate.convertAndSend(RESERVATION_QUEUE_NAME, reservationCompletedEvent);

        Order order = waitTillCompletedAndGetOrderFromDb(5000L);

        assertThat(order.getStatus(), is(OrderStatus.NEW));
        assertThat(order.getUserId(), is(USER_ID));
        assertThat(order.getReservationId(), is(nullValue()));
    }

    @Test
    public void testOrderDoesNotBecomeCompleteWhenExceptionIsThrownOnReservationFinalizedEvent() throws Exception {
        orderRepositorySpy.setThrowExceptionInSave(true);

        ReservationCompletedEvent reservationCompletedEvent = new ReservationCompletedEvent(
                RESERVATION_ID, orderId, USER_ID);

        jmsTemplate.convertAndSend(RESERVATION_QUEUE_NAME, reservationCompletedEvent);

        Order order = waitTillCompletedAndGetOrderFromDb(5000L);

        assertThat(order.getStatus(), is(OrderStatus.NEW));
        assertThat(order.getUserId(), is(USER_ID));
        assertThat(order.getReservationId(), is(nullValue()));
    }

    private Order waitTillCompletedAndGetOrderFromDb(long waitTimeout) throws InterruptedException {
        long stop = System.currentTimeMillis() + waitTimeout;
        Order order;
        do {
            order = orderRepositorySpy.findById(orderId).get();
            assertThat(order, is(notNullValue()));

            if (order.getStatus() == OrderStatus.COMPLETE) {
                break;
            } else {
                Thread.sleep(100L);
            }
        } while (System.currentTimeMillis() < stop);

        return order;
    }
}
