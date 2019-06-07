package rt.sagas.cart.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.CartAuthorizedEvent;

import javax.transaction.Transactional;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;
import static rt.sagas.events.QueueNames.CART_AUTHORIZED_EVENT_QUEUE;

@Component
public class CartEventsSender {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional(REQUIRES_NEW)
    public void sendCartAuthorizedEvent(String reservationId, String cartNumber, Long orderId, Long userId) {

        CartAuthorizedEvent cartAuthorizedEvent = new CartAuthorizedEvent(reservationId, cartNumber, orderId, userId);
        jmsTemplate.convertAndSend(CART_AUTHORIZED_EVENT_QUEUE, cartAuthorizedEvent);

        LOGGER.info("Cart Authorized Event sent: {}", cartAuthorizedEvent);
    }
}
