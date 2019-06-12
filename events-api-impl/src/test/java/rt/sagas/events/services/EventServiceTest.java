package rt.sagas.events.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import rt.sagas.events.Configuration;
import rt.sagas.events.TestConfiguration;
import rt.sagas.events.TestEvent;
import rt.sagas.events.listeners.JmsTestEventReceiver;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.events.entities.EventEntity;
import rt.sagas.events.repositories.EventRepository;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Configuration.class
})
@ContextConfiguration(classes = {
        TestConfiguration.class
})
public class EventServiceTest {

    @Autowired
    @Qualifier("testEventService")
    private EventService unit;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private JmsTestEventReceiver cartAuthorizedEventReceiver;

    private ObjectMapper objectMapper = new ObjectMapper();

    @After
    public void tearDown() {
        eventRepository.deleteAll();
        cartAuthorizedEventReceiver.clear();
    }

    @Test
    public void testTransactionEventIsStoredIntoDb() throws Exception {
        TestEvent event = new TestEvent("123456-1234-5678-ABCDEF");
        unit.storeOutgoingEvent(event);

        assertThat(eventRepository.count(), is(1L));
        EventEntity eventEntity = eventRepository.findAll().iterator().next();
        TestEvent entityEventFromDb = objectMapper.readValue(eventEntity.getEvent(),
                TestEvent.class);
        assertThat(entityEventFromDb, equalTo(event));
    }

    @Test
    public void testTransactionEventIsSentToTheQueue() throws Exception {
        unit.storeOutgoingEvent(new TestEvent("111111-1234-5678-AAABBBB"));

        unit.sendOutgoingEvents();

        TestEvent cartAuthorizedEvent = cartAuthorizedEventReceiver.pollEvent(
                e -> e.getEventMessage().equals("111111-1234-5678-AAABBBB"), 10000L);
        assertThat(cartAuthorizedEvent, is(notNullValue()));
    }

    @Test
    public void testMoreThanOneTransactionEventsAreSentToTheQueue() throws Exception {
        unit.storeOutgoingEvent(new TestEvent("111111-1234-5678-AAAAAAA"));
        unit.storeOutgoingEvent(new TestEvent("111111-1234-5678-BBBBBBB"));

        unit.sendOutgoingEvents();

        assertThat(cartAuthorizedEventReceiver.pollEvent(
                e -> e.getEventMessage().equals("111111-1234-5678-AAAAAAA"), 10000L),
                is(notNullValue()));
        assertThat(cartAuthorizedEventReceiver.pollEvent(
                e -> e.getEventMessage().equals("111111-1234-5678-BBBBBBB"), 10000L),
                is(notNullValue()));
    }

    @Test
    public void testSentEventsAreRemovedFromTheDb() throws Exception {
        unit.storeOutgoingEvent(new TestEvent("111111-1234-5678-CCCCCCC"));
        unit.storeOutgoingEvent(new TestEvent("111111-1234-5678-DDDDDDD"));
        assertThat(eventRepository.count(), is(2L));

        unit.sendOutgoingEvents();

        assertThat(eventRepository.count(), is(0L));
    }
}
