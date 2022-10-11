package backbonefx.mvvm;

import javafx.concurrent.Task;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Base class for creating a background task that can be passed to the {@link BackgroundCommand}.
 * The only difference from {@link Task} is that it allows setting user arg, which is used to
 * pass that arg from the {@link View}.
 * <p>
 * Implementations are free to ignore that arg or use it as they want to. The arg is always set
 * just before running the task.
 *
 * @param <T> input arg type
 * @param <V> return value type
 */
public abstract class BackgroundTask<T, V> extends Task<V> implements Consumer<T> {

    protected T arg;

    public @Nullable T getArg() {
        return arg;
    }

    @Override
    public void accept(@Nullable T arg) {
        this.arg = arg;
    }
}
