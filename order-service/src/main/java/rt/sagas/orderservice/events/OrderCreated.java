package rt.sagas.orderservice.events;

import rt.sagas.orderservice.entities.Order;

public class OrderCreated extends OrderEvent {

    private Long orderId;
    private Long userId;
    private String cartNumber;

    public OrderCreated() {
    }

    public OrderCreated(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.cartNumber = order.getCartNumber();
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
