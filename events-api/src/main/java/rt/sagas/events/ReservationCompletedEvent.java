package rt.sagas.events;

public class ReservationCompletedEvent extends SagaEvent {

    private String reservationId;
    private Long orderId;
    private Long userId;

    public ReservationCompletedEvent() {
    }

    public ReservationCompletedEvent(String reservationId, Long orderId, Long userId) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.userId = userId;
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
}