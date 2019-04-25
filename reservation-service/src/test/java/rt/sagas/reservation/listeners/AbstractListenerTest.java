package rt.sagas.reservation.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.repositories.ReservationRepository;

import java.util.List;

public class AbstractListenerTest {

    @Autowired
    protected ReservationRepository reservationRepository;

    public void tearDown() {
        reservationRepository.deleteAll();
    }

    protected List<Reservation> waitAndGetReservationsByOrderIdFromDb(Long orderId, long waitTimeout) throws Exception {
        long stop = System.currentTimeMillis() + waitTimeout;
        List<Reservation> reservations;
        do {
            reservations = reservationRepository.findAllByOrderId(orderId);
            if (reservations.size() > 0)
                break;
            else
                Thread.sleep(100L);
        } while (System.currentTimeMillis() < stop);

        return reservations;
    }
}
