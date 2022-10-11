package backbonefx.di;

import backbonefx.di.Feather;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SingletonTest {

    @Test
    public void testSimpleDependencyIsNotSingleton() {
        Feather feather = Feather.with();
        assertNotEquals(feather.instance(Plain.class), feather.instance(Plain.class));
    }

    @Test
    public void testSingletonIsSingleton() {
        Feather feather = Feather.with();
        assertEquals(feather.instance(SingletonObject.class), feather.instance(SingletonObject.class));
    }

    @Test
    public void testSingletonIsSingletonThroughProvider() {
        Feather feather = Feather.with();
        Provider<SingletonObject> provider = feather.provider(SingletonObject.class);
        assertEquals(provider.get(), provider.get());
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Plain { }

    @Singleton
    public static class SingletonObject { }
}
