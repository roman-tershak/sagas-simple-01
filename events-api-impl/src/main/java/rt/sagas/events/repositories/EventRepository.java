package rt.sagas.events.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rt.sagas.events.entities.EventEntity;

@Repository
public interface EventRepository extends CrudRepository<EventEntity, Long> {
}
