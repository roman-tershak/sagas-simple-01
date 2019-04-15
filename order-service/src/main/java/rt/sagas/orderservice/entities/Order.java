package rt.sagas.orderservice.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static rt.sagas.orderservice.entities.OrderStatus.NEW;

@Entity(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String cartNumber;
    private OrderStatus status = NEW;

    public Order() {
    }

    public Order(Long userId, String cartNumber) {
        this.userId = userId;
        this.cartNumber = cartNumber;
    }

    public Long getId() {
        return id;
    }

    @NotNull(message = "userId is mandatory")
    @Positive(message = "userId must be positive")
    public Long getUserId() {
        return userId;
    }

    @NotBlank(message = "cartNumber must not be blank")
    public String getCartNumber() {
        return cartNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

}
