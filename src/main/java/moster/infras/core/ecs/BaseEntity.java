package moster.infras.core.ecs;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.*;

/**
 * <pre>
 * 实体基类。
 * 一种最基础的实现，内部使用HashMap结构，获取所有组件时也根据优先级做了排序。
 * 如果对存取结构和遍历顺序有要求，可以考虑实现{@link IEntity}。
 * </pre>
 *
 * @author zhangfei
 */
public class BaseEntity implements IEntity {

    /**
     * 玩家自身的所有组件，key为组件类型，value为组件实例。
     */
    private final Map<Class<? extends IComponent>, IComponent> components = new HashMap<>();

    @Override
    public Collection<IComponent> getComponents() {
        List<IComponent> list = new ArrayList<>(this.components.size());
        list.addAll(components.values());
        // 排序时，Order值越小，优先级就越高，就越排在前面。
        // 没有标注Order时，优先级是最低的，默认值是Integer.MAX_VALUE
        // 这个排序是稳定的。
        AnnotationAwareOrderComparator.sort(list);
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(Class<T> clazz) {
        return (T) this.components.get(clazz);
    }

    @Override
    public void register(IComponent component) {
        this.components.put(component.getClass(), component);
    }

}
