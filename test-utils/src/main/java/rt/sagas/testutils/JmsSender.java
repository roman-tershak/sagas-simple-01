package rt.sagas.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import rt.sagas.events.SagaEvent;

import javax.transaction.Transactional;

@Component
public class JmsSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public void send(String queue, SagaEvent event) {
        jmsTemplate.convertAndSend(queue, event);
    }
}
