package rt.sagas.events;

public class TestEvent extends SagaEvent {

    private String eventMessage;

    public TestEvent() {
    }

    public TestEvent(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public String getEventMessage() {
        return eventMessage;
    }
}
