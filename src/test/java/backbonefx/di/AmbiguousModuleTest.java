package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.FeatherException;
import backbonefx.di.Provides;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class AmbiguousModuleTest {

    @Test
    public void testAmbiguousModuleThrowsException() {
        assertThatExceptionOfType(FeatherException.class)
                .isThrownBy(() -> Feather.with(new Module()));
    }

    public static class Module {

        @Provides
        String foo() {
            return "foo";
        }

        @Provides
        String bar() {
            return "bar";
        }
    }
}
