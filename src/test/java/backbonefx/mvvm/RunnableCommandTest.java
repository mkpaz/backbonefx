package backbonefx.mvvm;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RunnableCommandTest {

    @Test
    public void testRunnableCommand() {
        TestView v = new TestView();
        TestViewModel vm = v.getViewModel();

        assertThat(vm).isNotNull();
        assertThat(vm.positive.get()).isFalse();

        v.executeBar();
        v.executeBar();
        assertThat(vm.positive.get()).isFalse();
        assertThat(vm.counter.get()).isEqualTo(-1);

        v.executeFoo();
        assertThat(vm.positive.get()).isTrue();
        assertThat(vm.counter.get()).isZero();
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class TestView implements View<Node, TestViewModel> {

        final TestViewModel model = new TestViewModel();

        @Override
        public Node getRoot() {
            return null;
        }

        @Override
        public TestViewModel getViewModel() {
            return model;
        }

        void executeFoo() {
            model.foo.execute(null);
        }

        void executeBar() {
            model.bar.execute(null);
        }
    }

    public static class TestViewModel implements ViewModel {

        IntegerProperty counter = new SimpleIntegerProperty(-1);
        BooleanBinding positive = Bindings.createBooleanBinding(() -> counter.get() >= 0, counter);

        final RunnableCommand foo = new RunnableCommand(this::increment);
        final RunnableCommand bar = new RunnableCommand(this::increment, positive);

        void increment() {
            counter.set(counter.get() + 1);
        }
    }
}
