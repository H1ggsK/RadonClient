// File: EventManager.java
package skid.krypton.manager;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import skid.krypton.Krypton;
import skid.krypton.event.*;
import skid.krypton.event.EventListener;
import skid.krypton.module.Module;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class EventManager {
    private final Map<Class<?>, List<Listener>> EVENTS;
    private static final List<Reflections> reflections = new ArrayList<>();

    public EventManager() {
        this.EVENTS = new HashMap<>();
    }

    public void register(final Object o) {
        final Method[] declaredMethods = o.getClass().getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(EventListener.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                this.addListener(o, method, method.getAnnotation(EventListener.class));
            }
        }
    }

    private void addListener(final Object o, final Method method, final EventListener eventListener) {
        final Class<?> key = method.getParameterTypes()[0];
        method.setAccessible(true);
        this.EVENTS.computeIfAbsent(key, p0 -> new CopyOnWriteArrayList<>()).add(new Listener(o, method, eventListener.priority()));
        this.EVENTS.get(key).sort(Comparator.comparingInt(listener -> listener.getPriority().getValue()));
    }

    public void unregister(Object v12) {
        for (List<Listener> listeners : this.EVENTS.values()) {
            listeners.removeIf(v1 -> v1.getInstance() == v12);
        }
    }

    public void clear() {
        this.EVENTS.clear();
    }

    public void a(final Event event) {
        List<Listener> listeners = this.EVENTS.get(event.getClass());
        if (listeners == null) return;

        for (Listener listener : listeners) {
            try {
                Object holder = listener.getInstance();

                if (holder instanceof Module && !((Module) holder).isEnabled()) {
                    continue;
                }

                if (!event.isCancelled() || event instanceof CancellableEvent) {
                    listener.invoke(event);
                }
            } catch (Throwable _t) {
                System.err.println("Error dispatching event " + event.getClass().getSimpleName() + " to " + (listener.getInstance() != null ? listener.getInstance().getClass().getSimpleName() : "unknown"));
                _t.printStackTrace(System.err);
            }
        }
    }

    public static void b(final Event evt) {
        if (Krypton.INSTANCE == null || Krypton.INSTANCE.getEventBus() == null) return;
        Krypton.INSTANCE.getEventBus().a(evt);
    }

    // === ReflectInit logic integration ===

    public static void registerPackage(String pkg) {
        if (pkg != null && !pkg.isBlank()) {
            reflections.add(new Reflections(pkg, Scanners.MethodsAnnotated));
        }
    }

    public static void init(Class<? extends Annotation> annotation) {
        for (Reflections reflection : reflections) {
            Set<Method> initTasks = reflection.getMethodsAnnotatedWith(annotation);
            if (initTasks == null) continue;

            Map<Class<?>, List<Method>> byClass = initTasks.stream().collect(Collectors.groupingBy(Method::getDeclaringClass));
            Set<Method> left = new HashSet<>(initTasks);

            for (Method m; (m = left.stream().findAny().orElse(null)) != null; ) {
                reflectInit(m, annotation, left, byClass);
            }
        }
    }

    private static <T extends Annotation> void reflectInit(Method task, Class<T> annotation, Set<Method> left, Map<Class<?>, List<Method>> byClass) {
        left.remove(task);

        for (Class<?> clazz : getDependencies(task, annotation)) {
            for (Method m : byClass.getOrDefault(clazz, Collections.emptyList())) {
                if (left.contains(m)) {
                    reflectInit(m, annotation, left, byClass);
                }
            }
        }

        try {
            task.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Error running @" + annotation.getSimpleName() + " task '" + task.getDeclaringClass().getSimpleName() + "." + task.getName() + "'", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Method '" + task.getName() + "' uses Init annotations from non-static context", e);
        }
    }

    private static <T extends Annotation> Class<?>[] getDependencies(Method task, Class<T> annotation) {
        T init = task.getAnnotation(annotation);

        return switch (init) {
            case PreInit pre -> pre.dependencies();
            //case PostInit post -> post.dependencies();
            default -> new Class<?>[]{};
        };
    }
}
