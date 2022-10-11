/*
 * Copyright 2017 Zsolt Herpai (https://github.com/zsoltherpai/feather)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package backbonefx.di;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Feather is an ultra-lightweight dependency injection JSR-330 library. Dependency injection
 * frameworks are often perceived as "magical" and complex. Feather with just a few hundred
 * lines of code is probably the easiest, tiniest, most obvious one, and is quite efficient
 * too.
 *
 * Create Feather (the injector):
 * <pre>{@code Feather feather = Feather.with();}</pre>
 * An application typically needs a single Feather instance.
 *
 * <h2>Instantiating dependencies</h2>
 * Dependencies with @Inject constructor or a default constructor can be injected
 * by Feather without the need for any configuration.
 *
 * <pre>{@code
 * public class A {
 *     @Inject
 *     public A(B b) { ... }
 * }
 *
 * public class B {
 *     @Inject
 *     public B(C c, D d) { ... }
 * }
 *
 * public class C { ... }
 *
 * @Singleton
 * public class D { ... }
 * }</pre>
 *
 * Creating an instance of A:
 * <pre>{@code A a = feather.instance(A.class);}</pre>
 *
 * <h2>Providing additional dependencies to Feather</h2>
 * When injecting an interface, a 3rd party class or an object needing custom instantiation,
 * Feather relies on configuration modules providing those dependencies:
 *
 * <pre>{@code
 * public class MyModule {
 *     @Provides
 *     @Singleton
 *     DataSource dataSourceProvider() {
 *         DataSource dataSource = // instantiate some data source
 *         return dataSource;
 *     }
 * }
 * }</pre>
 *
 * Setting up Feather with module(s):
 * <pre>{@code Feather feather = Feather.with(new MyModule());}</pre>
 *
 * The DataSource dependency will now be available for injection:
 * <pre>{@code
 * public class MyApp {
 *     @Inject
 *     public MyApp(DataSource dataSource) { ... }
 * }
 * }</pre>
 *
 * Feather injects dependencies to @Provides methods arguments. This is particularly useful for
 * binding an implementation to an interface:
 *
 * <pre>{@code
 * public interface Foo { ... }
 *
 * public class FooBar implements Foo {
 *     @Inject
 *     public FooBar(X x, Y y, Z z) { ... }
 * }
 *
 * public class MyModule {
 *     @Provides
 *     Foo foo(FooBar fooBar) {
 *         return fooBar;
 *     }
 * }
 *
 * // injecting an instance of Foo interface will work using the MyModule above
 * public class A {
 *     @Inject
 *     public A(Foo foo) { ... }
 * }
 * }</pre>
 * Note that the @Provides serves just as a binding declaration here, no manual instantiation needed.
 *
 * <h2>Qualifiers</h2>
 *
 * Feather supports qualifiers, @Named or custom.
 * <pre>{@code
 * public class MyModule {
 *     @Provides
 *     @Named("greeting")
 *     String greeting() {
 *         return "hi";
 *     }
 *
 *     @Provides
 *     @SomeQualifier
 *     Foo some(FooSome fooSome) {
 *         return fooSome;
 *     };
 * }
 * }</pre>
 *
 * Injecting:
 * <pre>{@code
 * public class A {
 *     @Inject
 *     public A(@SomeQualifier Foo foo, @Named("greeting") String greet) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * Or directly from Feather:
 * <pre>{@code
 * String greet = feather.instance(String.class, "greeting");
 * Foo foo = feather.instance(Key.of(Foo.class, SomeQualifier.class));
 * }</pre>
 *
 * <h2>Provider injection</h2>
 * Feather injects providers to facilitate lazy loading or circular dependencies.
 *
 * <pre>{@code
 * public class A {
 *     @Inject
 *     public A(Provider<B> b) {
 *         B b = b.get(); // fetch a new instance when needed
 *     }
 * }
 * }</pre>
 *
 * Or getting a Provider directly from Feather:
 * <pre>{@code
 * Provider<B> provider = feather.provider(B.class);
 * }</pre>
 *
 * <h2>Override modules</h2>
 *
 * <pre>{@code
 * public class Module {
 *     @Provides
 *     DataSource dataSource() {
 *         // return a mysql datasource
 *     }
 * }
 *
 * public class TestModule extends Module {
 *     @Override
 *     @Provides
 *     DataSource dataSource() {
 *         // return a h2 datasource
 *     }
 * }
 * }</pre>
 *
 * <h2>Field injection</h2>
 * Feather supports constructor injection only when injecting to a dependency graph. It injects
 * fields also if it's explicitly triggered for a target object, e.g. to facilitate testing.
 *
 * <pre>{@code
 * public class AUnitTest {
 *     @Inject
 *     private Foo foo;
 *     @Inject
 *     private Bar bar;
 *
 *     @Before
 *     public void setUp() {
 *         Feather feather = // obtain a Feather instance
 *         feather.injectFields(this);
 *     }
 * }
 * }</pre>
 *
 * <h2>Method injection</h2>
 * Not supported. The need for it can be generally avoided by a Provider / SOLID design
 * (favoring immutability, injection via constructor).
 *
 * <h2>Post Construct</h2>
 * Feather can execute some initial object initialization after instantiation. You can
 * implement {@link Initializable} interface and put some initialization logic in here.
 * {@code @PostConstruct} annotation is deliberately not supported for the sake of simplicity.
 * Do not confuse {@code backbonefx.di.Initializable} with {@code javafx.fxml.Initializable}.
 * The latter is not supported to avoid javafx-fxml dependency.</p>
 *
 * <h2>How it works under the hood</h2>
 *
 * Feather is based on optimal use of reflection to provide dependencies. No code generating,
 * classpath scanning, proxying or anything costly involved.
 *
 * A simple example with some explanation:
 *
 * <pre>{@code
 * class A {
 *     @Inject
 *     A(B b) { ... }
 * }
 *
 * class B { ... }
 * }</pre>
 *
 * Without the use of Feather, class A could be instantiated with the following factory methods:
 * <pre>{@code
 * A a() {
 *     return new A(b());
 * }
 *
 * B b() {
 *     return new B();
 * }
 * }</pre>
 * <p>
 * Most of the information in these factories are redundant and they tend to be hot spots for
 * changes and sources for merge hells. Feather avoids the need for writing such factories by
 * doing the same thing internally.
 * When an instance of A is injected, Feather calls A's constructor with the necessary arguments
 * an instance of B. That instance of B is created the same way - a simple recursion, this time
 * with no further dependencies - and the instance of A is created.
 * </p>
 */
public class Feather {

    private final Map<Key<?>, Provider<?>> providers = new ConcurrentHashMap<>();
    private final Map<Key<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object[][]> injectFields = new ConcurrentHashMap<>(0);

    /** Constructs Feather with configuration modules */
    public static Feather with(Object... modules) {
        return new Feather(Arrays.asList(modules));
    }

    /** Constructs Feather with configuration modules */
    public static Feather with(Iterable<?> modules) {
        return new Feather(modules);
    }

    private Feather(Iterable<?> modules) {
        providers.put(Key.of(Feather.class), (Provider<Feather>) () -> Feather.this);

        for (final Object module : modules) {
            if (module instanceof Class c) {
                throw new FeatherException(String.format("%s provided as class instead of an instance.", c.getName()));
            }
            for (Method providerMethod : providers(module.getClass())) {
                providerMethod(module, providerMethod);
            }
        }
    }

    /** @return an instance of type */
    public <T> T instance(Class<T> type) {
        return provider(Key.of(type), null).get();
    }

    /** @return instance specified by key (type and qualifier) */
    public <T> T instance(Key<T> key) {
        return provider(key, null).get();
    }

    /** @return provider of type */
    public <T> Provider<T> provider(Class<T> type) {
        return provider(Key.of(type), null);
    }

    /** @return provider of key (type, qualifier) */
    public <T> Provider<T> provider(Key<T> key) {
        return provider(key, null);
    }

    /** Injects fields to the target object */
    public void injectFields(Object target) {
        if (!injectFields.containsKey(target.getClass())) {
            injectFields.put(target.getClass(), injectFields(target.getClass()));
        }
        for (Object[] f : injectFields.get(target.getClass())) {
            Field field = (Field) f[0];
            Key<?> key = (Key<?>) f[2];
            try {
                field.set(target, (boolean) f[1] ? provider(key) : instance(key));
            } catch (Exception e) {
                throw new FeatherException(
                        String.format("Can't inject field %s in %s", field.getName(), target.getClass().getName()),
                        e
                );
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Implementation                                                        //
    ///////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    private <T> Provider<T> provider(Key<T> key, Set<Key<?>> chain) {
        if (!providers.containsKey(key)) {
            final Constructor<?> constructor = constructor(key);
            final Provider<?>[] paramProviders = paramProviders(
                    key,
                    constructor.getParameterTypes(),
                    constructor.getGenericParameterTypes(),
                    constructor.getParameterAnnotations(),
                    chain
            );

            providers.put(key, singletonProvider(key, key.type.getAnnotation(Singleton.class), (Provider<?>) () -> {
                try {
                    Object o = constructor.newInstance(params(paramProviders));
                    if (o instanceof Initializable initializable) {
                        initializable.init();
                    }
                    return o;
                } catch (Exception e) {
                    throw new FeatherException(String.format("Can't instantiate %s", key), e);
                }
            }));
        }
        return (Provider<T>) providers.get(key);
    }

    private void providerMethod(Object module, Method m) {
        final Key<?> key = Key.of(m.getReturnType(), qualifier(m.getAnnotations()));
        if (providers.containsKey(key)) {
            throw new FeatherException(String.format("%s has multiple providers, module %s", key, module.getClass()));
        }

        Singleton singleton = m.getAnnotation(Singleton.class) != null ?
                m.getAnnotation(Singleton.class) :
                m.getReturnType().getAnnotation(Singleton.class);

        final Provider<?>[] paramProviders = paramProviders(
                key,
                m.getParameterTypes(),
                m.getGenericParameterTypes(),
                m.getParameterAnnotations(),
                Collections.singleton(key)
        );

        providers.put(key, singletonProvider(key, singleton, (Provider<?>) () -> {
            try {
                return m.invoke(module, params(paramProviders));
            } catch (Exception e) {
                throw new FeatherException(String.format("Can't instantiate %s with provider", key), e);
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private <T> Provider<T> singletonProvider(Key<?> key, Singleton singleton, Provider<T> provider) {
        return singleton != null ? () -> {
            if (!singletons.containsKey(key)) {
                synchronized (singletons) {
                    if (!singletons.containsKey(key)) {
                        singletons.put(key, provider.get());
                    }
                }
            }
            return (T) singletons.get(key);
        } : provider;
    }

    private Provider<?>[] paramProviders(Key<?> key,
                                         Class<?>[] parameterClasses,
                                         Type[] parameterTypes,
                                         Annotation[][] annotations,
                                         Set<Key<?>> chain) {
        Provider<?>[] providers = new Provider<?>[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; ++i) {
            Class<?> parameterClass = parameterClasses[i];
            Annotation qualifier = qualifier(annotations[i]);
            Class<?> providerType = Provider.class.equals(parameterClass) ?
                    (Class<?>) ((ParameterizedType) parameterTypes[i]).getActualTypeArguments()[0] :
                    null;

            if (providerType == null) {
                final Key<?> newKey = Key.of(parameterClass, qualifier);
                final Set<Key<?>> newChain = append(chain, key);
                if (newChain.contains(newKey)) {
                    throw new FeatherException(String.format("Circular dependency: %s", chain(newChain, newKey)));
                }
                providers[i] = () -> provider(newKey, newChain).get();
            } else {
                final Key<?> newKey = Key.of(providerType, qualifier);
                providers[i] = () -> provider(newKey, null);
            }
        }
        return providers;
    }

    private static Object[] params(Provider<?>[] paramProviders) {
        Object[] params = new Object[paramProviders.length];
        for (int i = 0; i < paramProviders.length; ++i) {
            params[i] = paramProviders[i].get();
        }
        return params;
    }

    private static Set<Key<?>> append(Set<Key<?>> set, Key<?> newKey) {
        if (set != null && !set.isEmpty()) {
            Set<Key<?>> appended = new LinkedHashSet<>(set);
            appended.add(newKey);
            return appended;
        } else {
            return Collections.singleton(newKey);
        }
    }

    private static Object[][] injectFields(Class<?> target) {
        Set<Field> fields = fields(target);
        Object[][] fs = new Object[fields.size()][];
        int i = 0;
        for (Field f : fields) {
            Class<?> providerType = f.getType().equals(Provider.class) ?
                    (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0] :
                    null;

            Class<?> keyType = providerType != null ? providerType : f.getType();
            fs[i++] = new Object[] { f, providerType != null, Key.of(keyType, qualifier(f.getAnnotations())) };
        }
        return fs;
    }

    private static Set<Field> fields(Class<?> type) {
        Class<?> current = type;
        Set<Field> fields = new HashSet<>();

        while (!current.equals(Object.class)) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }

        return fields;
    }

    private static String chain(Set<Key<?>> chain, Key<?> lastKey) {
        StringBuilder chainString = new StringBuilder();
        for (Key<?> key : chain) {
            chainString.append(key.toString()).append(" -> ");
        }
        return chainString.append(lastKey.toString()).toString();
    }

    private static Constructor<?> constructor(Key<?> key) {
        Constructor<?> inject = null;
        Constructor<?> noarg = null;

        for (Constructor<?> c : key.type.getDeclaredConstructors()) {
            if (c.isAnnotationPresent(Inject.class)) {
                if (inject == null) {
                    inject = c;
                } else {
                    throw new FeatherException(String.format("%s has multiple @Inject constructors", key.type));
                }
            } else if (c.getParameterTypes().length == 0) {
                noarg = c;
            }
        }

        Constructor<?> constructor = inject != null ? inject : noarg;
        if (constructor != null) {
            constructor.setAccessible(true);
            return constructor;
        } else {
            throw new FeatherException(String.format("%s doesn't have an @Inject or no-arg constructor, or a module provider", key.type.getName()));
        }
    }

    private static Set<Method> providers(Class<?> type) {
        Class<?> current = type;
        Set<Method> providers = new HashSet<>();

        while (!current.equals(Object.class)) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Provides.class) && (type.equals(current) || !providerInSubClass(method, providers))) {
                    method.setAccessible(true);
                    providers.add(method);
                }
            }
            current = current.getSuperclass();
        }

        return providers;
    }

    private static Annotation qualifier(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                return annotation;
            }
        }
        return null;
    }

    private static boolean providerInSubClass(Method method, Set<Method> discoveredMethods) {
        for (Method discovered : discoveredMethods) {
            if (discovered.getName().equals(method.getName()) &&
                    Arrays.equals(method.getParameterTypes(), discovered.getParameterTypes())) {
                return true;
            }
        }
        return false;
    }
}
