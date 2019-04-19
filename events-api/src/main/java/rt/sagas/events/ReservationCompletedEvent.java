package rt.sagas.events;

public class ReservationCompletedEvent {

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

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
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
}
