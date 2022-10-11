package backbonefx.mvvm;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BackgroundCommandTest {

    @Test
    public void testBackgroundCommand() {
        TestView v = new TestView();
        TestViewModel vm = v.getViewModel();

        // callback to be notified after each async command execution finished
        vm.launchCounter.addListener((obs, old, val) -> {
            if (val != null && val.intValue() == 4) {
                assertThat(vm.positive.get()).isTrue();
                assertThat(vm.counter.get()).isEqualTo(100);
            }
        });

        assertThat(vm).isNotNull();
        assertThat(vm.positive.get()).isFalse();

        v.executeBar(100);
        v.executeBar(100500);
        assertThat(vm.positive.get()).isFalse();
        assertThat(vm.counter.get()).isEqualTo(-1);

        v.executeFoo(1);
        v.executeFoo(100);
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
        IntegerProperty launchCounter = new SimpleIntegerProperty(0);

        final BackgroundCommand<Integer, Void> foo = new BackgroundCommand<>(() -> new TestBackgroundTask<>() {
            @Override
            protected Void call() {
                increment(getArg());
                return null;
            }

            @Override
            protected void done() {
                super.done();
                incrementLaunchCounter();
            }
        });

        final BackgroundCommand<Integer, Void> bar = new BackgroundCommand<>(() -> new TestBackgroundTask<>() {
            @Override
            protected Void call() {
                increment(getArg());
                return null;
            }

            @Override
            protected void done() {
                super.done();
                incrementLaunchCounter();
            }
        }, positive);

        synchronized void increment(Integer i) {
            counter.set(counter.get() + i);
        }

        synchronized void incrementLaunchCounter() {
            launchCounter.set(launchCounter.get() + 1);
        }
    }

    public abstract static class TestBackgroundTask<T, V> extends BackgroundTask<T, V> {

        boolean isFxApplicationThread() {
            return true;
        }
    }
}
