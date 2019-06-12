package rt.sagas.cart.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rt.sagas.cart.entities.EventEntity;

@Repository
public interface EventRepository extends CrudRepository<EventEntity, Long> {
}
