package backbonefx.di;

import backbonefx.di.Feather;
import backbonefx.di.Provides;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModuleOverrideTest {

    @Test
    public void testDependencyOverrideByModule() {
        assertEquals(
                PlainStub.class,
                Feather.with(new PlainStubOverrideModule()).instance(Plain.class).getClass()
        );
    }

    @Test
    public void testModuleOverwrittenBySubClass() {
        assertEquals("foo", Feather.with(new FooModule()).instance(String.class));
        assertEquals("bar", Feather.with(new BarModule()).instance(String.class));
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Plain { }

    public static class PlainStub extends Plain { }

    public static class PlainStubOverrideModule {

        @Provides
        public Plain plain(PlainStub plainStub) {
            return plainStub;
        }
    }

    public static class FooModule {

        @Provides
        String foo() {
            return "foo";
        }
    }

    public static class BarModule extends FooModule {

        @Provides
        @Override
        String foo() {
            return "bar";
        }
    }
}
