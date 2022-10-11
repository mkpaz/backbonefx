package backbonefx.event;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for events that provides some useful methods to avoid
 * boilerplate code.
 */
@SuppressWarnings("unused")
public abstract class AbstractEvent implements Event {

    protected final UUID id = UUID.randomUUID();
    protected final EventSource source;

    /** Creates new event without event source specified. */
    protected AbstractEvent() {
        this(null);
    }

    /** Creates new event from specified event source. */
    protected AbstractEvent(EventSource source) {
        this.source = source;
    }

    /** Returns unique event ID. */
    public UUID getId() {
        return id;
    }

    /** Returns unique event source. */
    public @Nullable EventSource getSource() {
        return source;
    }

    /** Checks whether event was sent from the specified event source or not. */
    public boolean isSentBy(EventSource source) {
        return Objects.equals(getSource(), source);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        AbstractEvent event = (AbstractEvent) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", source=" + source +
                '}';
    }
}
