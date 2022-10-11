package backbonefx.mvvm;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Executes the given {@link Runnable} action if the provided boolean
 * expression is true.
 */
public class RunnableCommand implements Command<Void>, Runnable {

    protected final Runnable action;
    protected final ObservableBooleanValue expression;

    /**
     * @see RunnableCommand#RunnableCommand(Runnable, ObservableBooleanValue)
     */
    public RunnableCommand(Runnable action) {
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
    public RunnableCommand(Runnable action,
                           @Nullable ObservableBooleanValue expression) {
        this.action = Objects.requireNonNull(action, "Action must not be null.");
        this.expression = Objects.requireNonNullElse(
                expression, new SimpleBooleanProperty(this, "expression", true)
        );
    }

    /** @inheritDoc */
    @Override
    public final boolean isExecutable() {
        return expression.get();
    }

    public ObservableBooleanValue executableProperty() {
        return expression;
    }

    /** @inheritDoc */
    @Override
    public void execute(Void arg) {
        if (isExecutable()) {
            action.run();
        }
    }

    /**
     * Convenient method to allow to call the command as lambda or method reference.
     */
    @Override
    public void run() {
        execute(null);
    }
}
