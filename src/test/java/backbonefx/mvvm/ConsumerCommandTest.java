package backbonefx.mvvm;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsumerCommandTest {

    @Test
    public void testConsumerCommand() {
        TestView v = new TestView();
        TestViewModel vm = v.getViewModel();

        assertThat(vm).isNotNull();
        assertThat(vm.positive.get()).isFalse();

        v.executeBar(100);
        v.executeBar(100500);
        assertThat(vm.positive.get()).isFalse();
        assertThat(vm.counter.get()).isEqualTo(-1);

        v.executeFoo(1);
        v.executeFoo(100);
        assertThat(vm.positive.get()).isTrue();
        assertThat(vm.counter.get()).isEqualTo(100);
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

        void executeFoo(int i) {
            model.foo.execute(i);
        }

        void executeBar(int i) {
            model.bar.execute(i);
        }
    }

    public static class TestViewModel implements ViewModel {

        IntegerProperty counter = new SimpleIntegerProperty(-1);
        BooleanBinding positive = Bindings.createBooleanBinding(() -> counter.get() >= 0, counter);

        final ConsumerCommand<Integer> foo = new ConsumerCommand<>(this::increment);
        final ConsumerCommand<Integer> bar = new ConsumerCommand<>(this::increment, positive);

        void increment(int i) {
            counter.set(counter.get() + i);
        }
    }
}
