package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.Key;
import backbonefx.di.Provides;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolymorphicDependencyTest {

    @Test
    public void testMultipleImplementationsSupported() {
        Feather feather = Feather.with(new Module());
        assertEquals(FooA.class, feather.instance(Key.of(Foo.class, "A")).getClass());
        assertEquals(FooB.class, feather.instance(Key.of(Foo.class, "B")).getClass());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Module {

        @Provides
        @Named("A")
        Foo a(FooA fooA) {
            return fooA;
        }

        @Provides
        @Named("B")
        Foo a(FooB fooB) {
            return fooB;
        }
    }

    interface Foo { }

    public static class FooA implements Foo {

        @Inject
        public FooA() { }
    }

    public static class FooB implements Foo {

        @Inject
        public FooB() { }
    }
}
