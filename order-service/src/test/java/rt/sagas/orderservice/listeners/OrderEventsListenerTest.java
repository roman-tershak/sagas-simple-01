package rt.sagas.orderservice.listeners;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.ReservationCompletedEvent;
import rt.sagas.orderservice.entities.Order;
import rt.sagas.orderservice.entities.OrderStatus;
import rt.sagas.orderservice.repositories.OrderRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static rt.sagas.events.QueueNames.RESERVATION_QUEUE_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderEventsListenerTest {

    private static final long USER_ID = 11L;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private OrderRepository orderRepository;

    private long orderId;

    @Before
    public void setUp() {
        Order order = new Order(USER_ID, "1234567890123456");
        final Order orderSaved = orderRepository.save(order);
        orderId = orderSaved.getId();
    }

    @Test
    public void testOrderBecomesCompletedOnReservationFinalizedEvent() throws Exception {
        final String reservationId = "ABCDEF-1234-8765-UVWXYZ";

        ReservationCompletedEvent reservationCompletedEvent = new ReservationCompletedEvent(
                reservationId, orderId, USER_ID
        );

        jmsTemplate.convertAndSend(RESERVATION_QUEUE_NAME, reservationCompletedEvent);

        long stop = System.currentTimeMillis() + 10000L;
        Order order = null;
        do {
            order = orderRepository.findById(orderId).get();
            System.out.println(order);
            assertThat(order, is(notNullValue()));

            if (order.getStatus() == OrderStatus.COMPLETE) {
                break;
            } else {
                Thread.sleep(100L);
            }

        } while (System.currentTimeMillis() < stop);

        assertThat(order.getStatus(), is(OrderStatus.COMPLETE));
        assertThat(order.getUserId(), is(USER_ID));
        assertThat(order.getReservationId(), is(reservationId));
    }
}
