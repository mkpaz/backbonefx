package backbonefx.event;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Default event bus implementation. Events are published in channels distinguished
 * by event type. Channels can be grouped using an event type hierarchy.
 */
@SuppressWarnings("unchecked")
public final class DefaultEventBus implements EventBus {

    private final Map<Class<?>, Set<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Creates new {@link EventBus} instance.
     * If you want to use global event bus go with singleton method instead.
     */
    public DefaultEventBus() { }

    ///////////////////////////////////////////////////////////////////////////

    /** @inheritDoc */
    @Override
    public <E extends Event> void subscribe(Class<? extends E> eventType, Consumer<E> subscriber) {
        Objects.requireNonNull(eventType, "Event type must be specified.");
        Objects.requireNonNull(subscriber, "Subscriber must not be null.");

        Set<Consumer<?>> eventSubscribers = getOrCreateSubscribers(eventType);
        eventSubscribers.add(subscriber);
    }

    /** @inheritDoc */
    @Override
    public <E extends Event> void unsubscribe(Consumer<E> subscriber) {
        if (subscriber == null) { return; }
        subscribers.values().forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    /** @inheritDoc */
    @Override
    public <E extends Event> void unsubscribe(Class<? extends E> eventType, Consumer<E> subscriber) {
        Objects.requireNonNull(eventType, "Event type must be specified.");
        Objects.requireNonNull(subscriber, "Subscriber must not be null.");

        subscribers.keySet().stream()
                .filter(eventType::isAssignableFrom)
                .map(subscribers::get)
                .forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    /** @inheritDoc */
    @Override
    public <E extends Event> void publish(E event) {
        Objects.requireNonNull(event, "Event must not be null.");

        Class<?> eventType = event.getClass();
        subscribers.keySet().stream()
                .filter(type -> type.isAssignableFrom(eventType))
                .flatMap(type -> subscribers.get(type).stream())
                .forEach(subscriber -> publish(event, (Consumer<E>) subscriber));
    }

    private <E> Set<Consumer<?>> getOrCreateSubscribers(Class<E> eventType) {
        Set<Consumer<?>> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            eventSubscribers = new CopyOnWriteArraySet<>();
            subscribers.put(eventType, eventSubscribers);
        }
        return eventSubscribers;
    }

    private <E extends Event> void publish(E event, Consumer<E> subscriber) {
        try {
            subscriber.accept(event);
        } catch (Exception e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }
}
