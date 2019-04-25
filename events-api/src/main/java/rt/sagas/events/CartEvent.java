package rt.sagas.events;

public class CartEvent extends SagaEvent {

    private String reservationId;
    private String cartNumber;
    private Long orderId;
    private Long userId;

    public CartEvent() {
    }

    public CartEvent(String reservationId, String cartNumber, Long orderId, Long userId) {
        this.reservationId = reservationId;
        this.cartNumber = cartNumber;
        this.orderId = orderId;
        this.userId = userId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getCartNumber() {
        return cartNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }
}
