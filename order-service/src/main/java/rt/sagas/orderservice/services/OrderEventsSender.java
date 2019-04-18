package rt.sagas.orderservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.orderservice.events.OrderEvent;

@Component
public class OrderEventsSender {

    @Autowired
    private JmsTemplate jmsTemplate;
//    @Autowired
//    private ObjectMapper objectMapper;

    public void sendOrderEvent(OrderEvent orderEvent) {

//        jmsTemplate.send("order.created", (session) -> {
//
//            String eventString;
//            try {
//                eventString = objectMapper.writeValueAsString(orderEvent);
//            } catch (JsonProcessingException e) {
//                throw new MessageConversionException("Error converting event to JSON", e);
//            }
//
//            return session.createTextMessage(eventString);
//        });
        jmsTemplate.convertAndSend("order.created", orderEvent);
    }
}
