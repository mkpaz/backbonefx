package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.Key;
import backbonefx.di.Provides;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NamedDependencyTest {

    @Test
    public void testNamedDependencyInstantiatedWithModule() {
        Feather feather = Feather.with(new Module());
        assertEquals("Hello!", feather.instance(Key.of(String.class, "hello")));
        assertEquals("Bye!", feather.instance(Key.of(String.class, "bye")));
    }

    public static class Module {

        @Provides
        @Named("hello")
        String hello() {
            return "Hello!";
        }

        @Provides
        @Named("bye")
        String bye() {
            return "Bye!";
        }
    }
}
