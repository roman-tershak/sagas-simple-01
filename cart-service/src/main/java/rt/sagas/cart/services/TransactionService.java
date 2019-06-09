package rt.sagas.cart.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rt.sagas.cart.entities.Transaction;
import rt.sagas.cart.entities.TransactionStatus;
import rt.sagas.cart.repositories.TransactionRepository;

import javax.transaction.Transactional;

import java.util.Optional;

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

        Optional<Transaction> mayAlreadyExist = transactionRepository.findByOrderId(orderId);
        if (mayAlreadyExist.isPresent()) {

            LOGGER.warn("Transaction {} has already been created", mayAlreadyExist.get());

        } else {
            Transaction transaction = new Transaction(cartNumber, orderId, userId, TransactionStatus.AUTHORIZED);
            transactionRepository.save(transaction);

            cartEventsSender.sendCartAuthorizedEvent(reservationId, cartNumber, orderId, userId);

            LOGGER.info("About to authorize Transaction: {}", transaction);
        }
    }

}
