package rt.sagas.events;

public class OrderCreatedEvent extends OrderEvent {

    private Long orderId;
    private Long userId;
    private String cartNumber;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, Long userId, String cartNumber) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartNumber = cartNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCartNumber() {
        return cartNumber;
    }
}
