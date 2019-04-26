package rt.sagas.order.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static rt.sagas.order.entities.OrderStatus.NEW;

@Entity(name = "ORDERS")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String cartNumber;
    private OrderStatus status = NEW;
    private String reservationId;

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

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getReservationId() {
        return this.reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Order{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", cartNumber='").append(cartNumber).append('\'');
        sb.append(", status=").append(status);
        sb.append(", reservationId='").append(reservationId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
