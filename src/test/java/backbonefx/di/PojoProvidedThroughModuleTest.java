package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.FeatherException;
import backbonefx.di.Provides;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("FieldCanBeLocal")
public class PojoProvidedThroughModuleTest {

    @Test
    public void testDependencyProvidedWithModule() {
        assertNotNull(Feather.with(new Module()).instance(Foo.class));
    }

    @Test
    public void testDependencyIsNotProvidedWithoutModule() {
        assertThatExceptionOfType(FeatherException.class)
                .isThrownBy(() -> Feather.with().instance(Foo.class));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Module {

        @Provides
        Foo foo() {
            return new Foo("foo");
        }
    }

    public static class Foo {

        private final String s;

        public Foo(String s) {
            this.s = s;
        }
    }
}
