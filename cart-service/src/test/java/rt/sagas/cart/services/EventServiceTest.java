package rt.sagas.cart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.cart.entities.EventEntity;
import rt.sagas.cart.listeners.JmsCartAuthorizedEventReceiver;
import rt.sagas.cart.repositories.EventRepository;
import rt.sagas.events.CartAuthorizedEvent;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService unit;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JmsCartAuthorizedEventReceiver cartAuthorizedEventReceiver;

    @After
    public void tearDown() {
        eventRepository.deleteAll();
        cartAuthorizedEventReceiver.clear();
    }

    @Test
    public void testTransactionEventIsStoredIntoDb() throws Exception {
        CartAuthorizedEvent event = new CartAuthorizedEvent(
                "123456-1234-5678-ABCDEF", "1234567890123456", 123L, 12L);
        unit.storeOutgoingEvent(event);

        assertThat(eventRepository.count(), is(1L));
        EventEntity eventEntity = eventRepository.findAll().iterator().next();
        CartAuthorizedEvent entityEventFromDb = objectMapper.readValue(eventEntity.getEvent(),
                CartAuthorizedEvent.class);
        assertThat(entityEventFromDb, equalTo(event));
    }

    @Test
    public void testTransactionEventIsSentToTheQueue() throws Exception {
        unit.storeOutgoingEvent(new CartAuthorizedEvent(
                "111111-1234-5678-AAABBBB", "22222222233333333", 232L, 23L));

        unit.sendOutgoingEvents();

        CartAuthorizedEvent cartAuthorizedEvent = cartAuthorizedEventReceiver.pollEvent(
                e -> e.getReservationId().equals("111111-1234-5678-AAABBBB"), 10000L);
        assertThat(cartAuthorizedEvent, is(notNullValue()));
        assertThat(cartAuthorizedEvent.getCartNumber(), is("22222222233333333"));
        assertThat(cartAuthorizedEvent.getOrderId(), is(232L));
        assertThat(cartAuthorizedEvent.getUserId(), is(23L));
    }

    @Test
    public void testMoreThanOneTransactionEventsAreSentToTheQueue() throws Exception {
        unit.storeOutgoingEvent(new CartAuthorizedEvent(
                "111111-1234-5678-AAAAAAA", "222222222222222222", 222L, 22L));
        unit.storeOutgoingEvent(new CartAuthorizedEvent(
                "111111-1234-5678-BBBBBBB", "333333333333333333", 333L, 33L));

        unit.sendOutgoingEvents();

        assertThat(cartAuthorizedEventReceiver.pollEvent(
                e -> {
                    return e.getReservationId().equals("111111-1234-5678-AAAAAAA") &&
                            e.getCartNumber().equals("222222222222222222") &&
                            e.getOrderId().equals(222L) &&
                            e.getUserId().equals(22L);
                }, 10000L), is(notNullValue()));
        assertThat(cartAuthorizedEventReceiver.pollEvent(
                e -> {
                    return e.getReservationId().equals("111111-1234-5678-BBBBBBB") &&
                            e.getCartNumber().equals("333333333333333333") &&
                            e.getOrderId().equals(333L) &&
                            e.getUserId().equals(33L);
                }, 10000L), is(notNullValue()));
    }

    @Test
    public void testSentEventsAreRemovedFromTheDb() throws Exception {
        unit.storeOutgoingEvent(new CartAuthorizedEvent(
                "111111-1234-5678-CCCCCCC", "444444444444444444", 444L, 44L));
        unit.storeOutgoingEvent(new CartAuthorizedEvent(
                "111111-1234-5678-DDDDDDD", "555555555555555555", 555L, 55L));
        assertThat(eventRepository.count(), is(2L));

        unit.sendOutgoingEvents();

        assertThat(eventRepository.count(), is(0L));
    }
}
