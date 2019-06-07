package rt.sagas.cart.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.cart.entities.Transaction;
import rt.sagas.cart.entities.TransactionStatus;
import rt.sagas.cart.repositories.TransactionRepository;

import javax.transaction.Transactional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
public class TransactionService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CartEventsSender cartEventsSender;

    @Transactional(REQUIRES_NEW)
    public void authorizeTransaction(String reservationId, Long orderId, Long userId, String cartNumber) {

        cartEventsSender.sendCartAuthorizedEvent(reservationId, cartNumber, orderId, userId);

        Transaction transaction = new Transaction(cartNumber, orderId, userId, TransactionStatus.AUTHORIZED);
        transactionRepository.save(transaction);

        LOGGER.info("About to authorize Transaction: {}", transaction);
    }

}
