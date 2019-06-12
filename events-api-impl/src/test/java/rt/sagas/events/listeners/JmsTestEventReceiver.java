package rt.sagas.events.listeners;

import rt.sagas.events.TestEvent;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rt.sagas.events.TestConfiguration;
import rt.sagas.testutils.JmsReceiver;

import javax.jms.TextMessage;
import javax.transaction.Transactional;

@Component
public class JmsTestEventReceiver extends JmsReceiver<TestEvent> {

    public JmsTestEventReceiver() {
        super(TestEvent.class);
    }

    @Transactional
    @JmsListener(destination = TestConfiguration.TEST_DESTINATION)
    public void receiveMessage(@Payload TextMessage textMessage) throws Exception {
        super.receiveMessage(textMessage.getText());
    }
}
