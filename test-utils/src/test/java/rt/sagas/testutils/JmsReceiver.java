package rt.sagas.testutils;

import rt.sagas.events.SagaEvent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class JmsReceiver<E extends SagaEvent> {

    private LinkedBlockingQueue<E> events = new LinkedBlockingQueue<>();

    public abstract void receiveMessage(E event);

    public void addEvent(E event) {
        events.add(event);
    }

    public E pollEvent() throws InterruptedException {
        return pollEvent(10000L);
    }

    public E pollEvent(long timeout) throws InterruptedException {
        return events.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public void clear() {
        events.clear();
    }
}
