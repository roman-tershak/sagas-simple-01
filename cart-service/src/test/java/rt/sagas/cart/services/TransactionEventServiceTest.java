package rt.sagas.cart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rt.sagas.cart.entities.EventEntity;
import rt.sagas.cart.repositories.TransactionEventRepository;
import rt.sagas.events.CartAuthorizedEvent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionEventServiceTest {

    @Autowired
    private TransactionEventService unit;
    @Autowired
    private TransactionEventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testTransactionEventIsStoredIntoDb() throws Exception {
        String reservationId = "123456-1234-5678-ABCDEF";
        String cartNumber = "1234567890123456";
        Long orderId = 123L;
        Long userId = 12L;
        CartAuthorizedEvent event = new CartAuthorizedEvent(reservationId, cartNumber, orderId, userId);
        unit.storeOutgoingEvent(event);

        assertThat(eventRepository.count(), is(1L));
        EventEntity eventEntity = eventRepository.findAll().iterator().next();
        CartAuthorizedEvent entityEventFromDb = objectMapper.readValue(eventEntity.getEvent(),
                CartAuthorizedEvent.class);
        assertThat(entityEventFromDb, equalTo(event));
    }
}
