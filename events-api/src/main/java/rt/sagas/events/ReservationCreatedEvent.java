package rt.sagas.events;

public class ReservationCreatedEvent extends ReservationEvent {

    private String cartNumber;

    public ReservationCreatedEvent() {
    }

    public ReservationCreatedEvent(String reservationId, Long orderId, Long userId, String cartNumber) {
        super(reservationId, orderId, userId);

        this.cartNumber = cartNumber;
    }

    public String getCartNumber() {
        return cartNumber;
    }
}
