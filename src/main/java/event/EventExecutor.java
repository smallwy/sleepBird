package event;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import event.annontation.Event;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 事件管理执行器，支持事件注册和触发，触发支持多个优先级，不再优先级内的事件监听器将被屏蔽
 **/
public class EventExecutor {
    private static Logger logger = LoggerFactory.getLogger(EventExecutor.class);

    private final Map<Class<? extends IEvent>, Collection<EventHandler>> bindings;
    private final Set<EventListener> registeredListeners;

    public EventExecutor() {
        this.bindings = new HashMap<>();
        this.registeredListeners = new HashSet<>();
    }

    public List<EventHandler> getListenersFor(Class<? extends IEvent> clazz) {
        if (!this.bindings.containsKey(clazz)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.bindings.get(clazz));
    }

    public <T extends IEvent> T executeEvent(T event, int priority) {
        Collection<EventHandler> handlers = this.bindings.get(event.getClass());
        if (handlers == null) {
            return event;
        }

        for (EventHandler handler : handlers) {
            if (priority == EventPriority.PRE && handler.getPriority() > priority)
                continue;
            if (priority == EventPriority.POST && handler.getPriority() < priority)
                continue;
            handler.execute(event);
        }

        return event;
    }

    public <T extends IEvent> T executeEvent(T event) {
        return this.executeEvent(event, EventPriority.ALL);
    }

    public void registerListener(final EventListener listener) {
        if (this.registeredListeners.contains(listener)) {
            logger.error("Listener already registered: {}", listener);
            return;
        }

        Method[] methods = listener.getClass().getDeclaredMethods();
        this.registeredListeners.add(listener);
        for (final Method method : methods) {
            Event annotation = method.getAnnotation(Event.class);
            if (annotation == null)
                continue;

            if (!method.getReturnType().equals(void.class)) {
                logger.error("Ignoring method due to non-void return: {}", method.getName());
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1)
                continue;


            Class<?> param = parameters[0];
            if (IEvent.class.isAssignableFrom(param)) {
                @SuppressWarnings("unchecked")
                Class<? extends IEvent> readParam = (Class<? extends IEvent>) param;

                if (!this.bindings.containsKey(readParam)) {
                    this.bindings.put(readParam, new TreeSet<>());
                }

                Collection<EventHandler> eventHandlerForEvent = this.bindings.get(readParam);
                eventHandlerForEvent.add(new EventHandler(listener, method, annotation));
            }
        }
    }

    public void removeListener(final EventListener listener) {
        for (Map.Entry<Class<? extends IEvent>, Collection<EventHandler>> ee : this.bindings.entrySet()) {
            ee.getValue().removeIf(handler -> handler.getListener() == listener);
        }
        this.registeredListeners.remove(listener);
    }

    public void clear() {
        this.bindings.clear();
        this.registeredListeners.clear();
    }
}
