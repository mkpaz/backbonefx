package backbonefx.di;

import backbonefx.di.Feather;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FieldInjectionTest {

    @Test
    public void testAllFieldsInjected() {
        Target target = new Target();
        Feather.with().injectFields(target);
        assertNotNull(target.foo);
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Foo { }

    public static class Target {

        @Inject
        private Foo foo;
    }
}
