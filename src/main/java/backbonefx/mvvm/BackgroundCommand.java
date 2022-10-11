package backbonefx.mvvm;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Executes the given {@link BackgroundTask} action if the provided boolean
 * expression is true.
 * <p>
 * Each subsequent command execution creates a new task using user provider
 * factory {@link Supplier} and runs it in the background in a new thread.
 * You can provide custom {@link Executor} service to submit the new tasks.
 */
public class BackgroundCommand<T, V> implements Command<T> {

    protected final Supplier<BackgroundTask<T, V>> actionSupplier;
    protected final ObservableBooleanValue expression;
    protected final Executor executor;

    /**
     * @see BackgroundCommand#BackgroundCommand(Supplier, ObservableBooleanValue, Executor)
     */
    public BackgroundCommand(Supplier<BackgroundTask<T, V>> actionSupplier) {
        this(actionSupplier, null, null);
    }

    /**
     * @see BackgroundCommand#BackgroundCommand(Supplier, ObservableBooleanValue, Executor)
     */
    public BackgroundCommand(Supplier<BackgroundTask<T, V>> actionSupplier,
                             @Nullable ObservableBooleanValue expression) {
        this(actionSupplier, expression, null);
    }

    /**
     * Create new command with given action, expression and executor. If expression is
     * omitted, it means that the command is always (unconditionally) executable. And if
     * executor is omitted, it means that each task will be run in a new {@link Thread}
     * instance.
     *
     * @param actionSupplier background task factory
     * @param expression     boolean expression to determine whether provided action
     *                       can be executed at the moment or not
     * @param executor       thread factory for submitting new tasks
     */
    public BackgroundCommand(Supplier<BackgroundTask<T, V>> actionSupplier,
                             @Nullable ObservableBooleanValue expression,
                             @Nullable Executor executor) {
        this.actionSupplier = Objects.requireNonNull(actionSupplier, "Action must not be null.");
        this.expression = Objects.requireNonNullElse(
                expression, new SimpleBooleanProperty(this, "expression", true)
        );
        this.executor = executor;
    }

    @Override
    public boolean isExecutable() {
        return expression.get();
    }

    public ObservableBooleanValue executableProperty() {
        return expression;
    }

    @Override
    public void execute(@Nullable T arg) {
        if (isExecutable()) {
            runTask(arg);
        }
    }

    protected void runTask(@Nullable T arg) {
        BackgroundTask<T, V> task = actionSupplier.get();
        task.accept(arg);

        if (executor != null) {
            executor.execute(task);
        } else {
            new Thread(task).start();
        }
    }
}
