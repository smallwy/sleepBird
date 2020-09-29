package event;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import event.annontation.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandler implements Comparable<EventHandler> {
    private static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private final EventListener listener;
    private final Method method;
    private final Event annotation;

    public EventHandler(EventListener listener, Method method, Event annotation) {
        this.listener = listener;
        this.method = method;
        this.annotation = annotation;
    }

    public EventListener getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }

    public Event getAnnotation() {
        return annotation;
    }

    public int getPriority() {
        return this.annotation.priority();
    }

    public void execute(IEvent event) {
        try {
            this.method.invoke(this.listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Exception when performing {} for event {}", this.toString(), event.toString(), e);
        }
    }

    @Override
    public String toString() {
        return "EventHandler{" +
                "listener=" + listener +
                ", method=" + method.getName() +
                '}';
    }

    @Override
    public int compareTo(EventHandler o) {
        int c = this.annotation.priority() - o.annotation.priority();
        if (c == 0)
            c = this.listener.hashCode() - o.listener.hashCode();
        return c == 0 ? this.hashCode() - o.hashCode() : c;
    }

}