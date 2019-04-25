package rt.sagas.events;

public class ReservationErrorEvent extends ReservationEvent {

    private String cartNumber;
    private String message;

    public ReservationErrorEvent() {
    }

    public ReservationErrorEvent(String reservationId, Long orderId, Long userId, String cartNumber, String message) {
        super(reservationId, orderId, userId);

        this.cartNumber = cartNumber;
        this.message = message;
    }

    public String getCartNumber() {
        return cartNumber;
    }

    public String getMessage() {
        return message;
    }
}
