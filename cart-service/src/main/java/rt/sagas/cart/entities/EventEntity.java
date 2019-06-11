package rt.sagas.cart.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "TRANSACTION_EVENTS")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String event;

    public EventEntity() {
    }

    public EventEntity(String event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }
}
