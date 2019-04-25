package rt.sagas.events;

public class ReservationConfirmedEvent extends ReservationEvent {

    public ReservationConfirmedEvent() {
    }

    public ReservationConfirmedEvent(String reservationId, Long orderId, Long userId) {
        super(reservationId, orderId, userId);
    }

}
