package backbonefx.mvvm;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Calls the given {@link Consumer} action if the provided boolean
 * expression is true.
 *
 * @param <T> input arg type
 */
public class ConsumerCommand<T> implements Command<T>, Consumer<T> {

    protected final Consumer<T> action;
    protected final ObservableBooleanValue expression;

    /**
     * @see ConsumerCommand#ConsumerCommand(Consumer, ObservableBooleanValue)
     */
    public ConsumerCommand(Consumer<T> action) {
        this(action, null);
    }

    /**
     * Create new command with given action and expression. The latter can be omitted
     * which means that the command is always (unconditionally) executable.
     *
     * @param action     an action to execute
     * @param expression boolean expression to determine whether provided action
     *                   can be executed at the moment or not
     */
    public ConsumerCommand(Consumer<T> action,
                           @Nullable ObservableBooleanValue expression) {
        this.action = Objects.requireNonNull(action, "Action must not be null.");
        this.expression = Objects.requireNonNullElse(
                expression, new SimpleBooleanProperty(this, "expression", true)
        );
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isExecutable() {
        return expression.get();
    }

    public ObservableBooleanValue executableProperty() {
        return expression;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(@Nullable T arg) {
        if (isExecutable()) {
            action.accept(arg);
        }
    }

    /**
     * Convenient method to allow to call the command as lambda or method reference.
     */
    @Override
    public void accept(@Nullable T arg) {
        execute(arg);
    }
}
