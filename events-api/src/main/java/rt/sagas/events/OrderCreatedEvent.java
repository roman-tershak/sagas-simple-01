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

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(String cartNumber) {
        this.cartNumber = cartNumber;
    }
}
