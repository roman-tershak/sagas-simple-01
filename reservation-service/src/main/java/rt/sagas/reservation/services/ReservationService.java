package rt.sagas.reservation.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.reservation.entities.Reservation;
import rt.sagas.reservation.entities.ReservationFactory;
import rt.sagas.reservation.repositories.ReservationRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;
import static rt.sagas.reservation.entities.ReservationStatus.CONFIRMED;
import static rt.sagas.reservation.entities.ReservationStatus.PENDING;

@Service
public class ReservationService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ReservationFactory reservationFactory;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationEventsSender reservationEventsSender;

    @Transactional(REQUIRES_NEW)
    public void createReservation(Long orderId, Long userId, String cartNumber) {
        List<Reservation> reservationsByOrderId = reservationRepository.findAllByOrderId(orderId);

        if (reservationsByOrderId.size() == 0) {
            Reservation reservation = reservationFactory.createNewPendingReservationFor(orderId, userId);
            reservationRepository.save(reservation);

            reservationEventsSender.sendReservationCreatedEvent(
                    reservation.getId(), orderId, userId, cartNumber);
        } else {
            LOGGER.warn("Reservations for Order Id {} has already been created", orderId);
        }
    }

    @Transactional(REQUIRES_NEW)
    public void confirmReservation(String reservationId, Long orderId, Long userId) {

        Optional<Reservation> optional = reservationRepository.findById(reservationId);

        if (optional.isPresent()) {
            Reservation reservation = optional.get();

            if (reservation.getStatus() == PENDING) {

                reservation.setStatus(CONFIRMED);
                reservationRepository.save(reservation);

                reservationEventsSender.sendReservationConfirmedEvent(
                        reservationId, orderId, userId);
            } else {
                LOGGER.warn("Reservation: {} is not PENDING, skipping", reservation);
            }
        } else {
            Reservation reservation = reservationFactory.createNewPendingReservationFor(orderId, userId);
            reservation.setStatus(CONFIRMED);
            reservationRepository.save(reservation);

            reservationEventsSender.sendReservationConfirmedEvent(
                    reservationId, orderId, userId);

            LOGGER.warn("Reservation: {} did not exist, creating it", reservation);
        }
    }
}
