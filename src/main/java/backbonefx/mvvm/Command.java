package backbonefx.mvvm;

import org.jetbrains.annotations.Nullable;

/** Basic command interface. */
public interface Command<T> {

    /**
     * Determines whether the command executable (meets user specified conditions) or not.
     * There's no corresponding observable property here to allow implementations to use
     * e.g. {@link java.util.function.Predicate} or whatever is more appropriate from the
     * user point of view. If you need an observable value, use one of the standard
     * implementations.
     */
    boolean isExecutable();

    /**
     * Executes the command. The method can optionally accept input arg, which can be used
     * or not in implementations.
     */
    void execute(@Nullable T arg);
}
