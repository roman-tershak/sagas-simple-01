package rt.sagas.events;

public class CartAuthorizedEvent extends CartEvent {

    public CartAuthorizedEvent() {
    }

    public CartAuthorizedEvent(String reservationId, String cartNumber, Long orderId, Long userId) {
        super(reservationId, cartNumber, orderId, userId);
    }

}
