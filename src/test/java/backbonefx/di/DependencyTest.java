package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.FeatherException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("unused")
public class DependencyTest {

    @Test
    public void testDependencyInstantiationSupported() {
        assertNotNull(Feather.with().instance(Foo.class));
    }

    @Test
    public void testProviderInstantiationSupported() {
        assertNotNull(Feather.with().provider(Foo.class).get());
    }

    @Test
    public void testUnknownDependencyThrowsException() {
        assertThatExceptionOfType(FeatherException.class)
                .isThrownBy(() -> Feather.with().instance(Unknown.class));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Foo { }

    public static class Unknown {

        public Unknown(String noSuitableConstructor) { }
    }
}


