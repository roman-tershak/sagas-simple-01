package rt.sagas.testutils;

import rt.sagas.events.SagaEvent;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

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

    public E pollEvent(Predicate<E> predicate) throws InterruptedException {
        return pollEvent(predicate, 10000L);
    }

    public E pollEvent(Predicate<E> predicate, long timeout) throws InterruptedException {
        E event = null;
        long stop = System.currentTimeMillis() + timeout;
        do {
            Optional<E> optional = events.stream().filter(predicate).findFirst();
            if (optional.isPresent()) {
                event = optional.get();
                events.remove(event);
                break;
            } else {
                Thread.sleep(50L);
            }
        } while (System.currentTimeMillis() < stop);

        return event;
    }

    public void clear() {
        events.clear();
    }
}
