package backbonefx.di;

import backbonefx.di.Feather;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransitiveDependencyTest {

    @Test
    public void testTransitiveDependenciesSupported() {
        Feather feather = Feather.with();
        A a = feather.instance(A.class);
        assertNotNull(a.b.c);
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class A {

        private final B b;

        @Inject
        public A(B b) {
            this.b = b;
        }
    }

    public static class B {

        private final C c;

        @Inject
        public B(C c) {
            this.c = c;
        }
    }

    public static class C { }
}
