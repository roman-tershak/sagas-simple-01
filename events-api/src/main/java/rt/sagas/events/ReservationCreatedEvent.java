package rt.sagas.events;

public class ReservationCreatedEvent extends SagaEvent {

    private String reservationId;
    private Long orderId;
    private Long userId;
    private String cartNumber;

    public ReservationCreatedEvent() {
    }

    public ReservationCreatedEvent(String reservationId, Long orderId, Long userId, String cartNumber) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.userId = userId;
        this.cartNumber = cartNumber;
    }

    public String getReservationId() {
        return reservationId;
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
