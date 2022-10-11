package backbonefx.event;

import org.jetbrains.annotations.Nullable;

public class TestEvent<T> extends AbstractEvent {

    private final T value;

    public TestEvent(EventSource source, T value) {
        this.value = value;
    }

    public @Nullable T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BaseEvent{" +
                "value=" + value +
                "} " + super.toString();
    }
}
