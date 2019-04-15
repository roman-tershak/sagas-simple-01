package rt.sagas.orderservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rt.sagas.orderservice.entities.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
