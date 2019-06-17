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
import java.util.ArrayList;
import java.util.List;
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
    public void storeOutgoingEvent(String destination, SagaEvent event) throws Exception {

        eventRepository.save(
                new EventEntity(
                        destination, objectMapper.writeValueAsString(event)));

        LOGGER.info("Stored outgoing Event {} for {}", event, destination);
    }

    @Transactional(REQUIRES_NEW)
    public void sendOutgoingEvents() {
        List<Long> ids = new ArrayList<>();

        eventRepository.findAll().forEach(eventEntity -> {

            eventSender.sendEvent(eventEntity.getDestination(), eventEntity.getEvent());
            ids.add(eventEntity.getId());
        });

        int idsCount = ids.size();
        if (idsCount > 0) {
            eventRepository.deleteByIds(ids);
        }

        if (idsCount != 1) {
            LOGGER.info("A batch of events {} sent", idsCount);
        }
    }
}
