package com.h1ggsk.radon.manager;

import com.h1ggsk.radon.Radon;
import org.reflections.Reflections;
import com.h1ggsk.radon.event.*;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.module.Module;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager {
    private final Map<Class<?>, List<Listener>> EVENTS;
    private static final List<Reflections> reflections = new ArrayList<>();

    public EventManager() {
        this.EVENTS = new HashMap<>();
    }

    public void register(final Object object) {
        final Method[] declaredMethods = object.getClass().getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(EventListener.class) && method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                this.addListener(object, method, method.getAnnotation(EventListener.class));
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

    public void dispatch(final Event event) {
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

    public static void throwEvent(final Event evt) {
        if (Radon.INSTANCE == null || Radon.INSTANCE.getEventBus() == null) return;
        Radon.INSTANCE.getEventBus().dispatch(evt);
    }
}
