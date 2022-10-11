package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.Provides;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelfProvidingFeatherTest {

    @Test
    public void testFeatherCanInjectItself() {
        final Feather feather = Feather.with(new Module());
        assertEquals(Integer.valueOf(feather.hashCode()), feather.instance(Integer.class));
    }

    public static class Module {

        @Provides
        public Integer feather(Feather feather) {
            return feather.hashCode();
        }
    }
}
