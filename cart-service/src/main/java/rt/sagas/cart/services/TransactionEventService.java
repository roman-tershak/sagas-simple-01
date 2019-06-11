package rt.sagas.cart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.cart.entities.EventEntity;
import rt.sagas.cart.repositories.TransactionEventRepository;
import rt.sagas.events.CartAuthorizedEvent;

@Service
public class TransactionEventService {

    @Autowired
    private TransactionEventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;

    public void storeOutgoingEvent(CartAuthorizedEvent event) throws Exception {
        EventEntity eventEntity = new EventEntity(objectMapper.writeValueAsString(event));
        eventRepository.save(eventEntity);
    }
}
