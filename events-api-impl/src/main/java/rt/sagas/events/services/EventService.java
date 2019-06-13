package rt.sagas.events.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.events.SagaEvent;
import rt.sagas.events.entities.EventEntity;
import rt.sagas.events.repositories.EventRepository;

import javax.transaction.Transactional;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class EventService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventSender eventSender;

    @Transactional(REQUIRED)
    public void storeOutgoingEvent(SagaEvent event) throws Exception {
        EventEntity eventEntity = new EventEntity(objectMapper.writeValueAsString(event));
        eventRepository.save(eventEntity);

        LOGGER.info("Event {} enqueued", event);
    }

    @Transactional(REQUIRES_NEW)
    public void sendOutgoingEvents(String destination) {
        AtomicInteger count = new AtomicInteger();

        eventRepository.findAll().forEach(eventEntity -> {

            eventSender.sendEvent(destination, eventEntity.getEvent());
            eventRepository.delete(eventEntity);

            count.incrementAndGet();
        });

        if (count.get() != 1) {
            LOGGER.info("A batch of events {} sent to {}", count, destination);
        }
    }
}
