package backbonefx.mvvm;

import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.Nullable;

/** Basic View interface. */
@SuppressWarnings("unused")
public interface View<V extends Node, M extends ViewModel> {

    /**
     * Returns the root element of the view.
     * This method is for adding (or removing) view to the scene graph.
     */
    V getRoot();

    /**
     * Returns the {@link ViewModel} responsible the view.
     * Each view have to be connected to one and only one view model.
     */
    M getViewModel();

    /**
     * Resets the view to its default state. In most cases this should be delegated to the
     * {@link ViewModel}. Default implementation does nothing, override and put some appropriate
     * logic inside the method.
     */
    default void reset() { }

    /**
     * Returns the {@link Stage} instance for this view, if present.
     * It will be null until the root node is added to the scene.
     */
    default @Nullable Window getWindow() {
        return getRoot() != null && getRoot().getScene() != null ? getRoot().getScene().getWindow() : null;
    }
}
