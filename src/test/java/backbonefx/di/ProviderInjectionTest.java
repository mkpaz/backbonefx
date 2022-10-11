package backbonefx.di;

import backbonefx.di.Feather;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProviderInjectionTest {

    @Test
    public void testProviderCanBeInjected() {
        assertNotNull(Feather.with().instance(Foo.class).barProvider.get());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Foo {

        private final Provider<Bar> barProvider;

        @Inject
        public Foo(Provider<Bar> barProvider) {
            this.barProvider = barProvider;
        }
    }

    public static class Bar { }
}
