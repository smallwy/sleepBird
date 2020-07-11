package moster.infras.core.ecs;

import java.util.Collection;

/**
 * ECS结构中的实体，负责维护组件的生命周期。
 *
 * @author zhangfei
 */
public interface IEntity {

    /**
     * 获取某个组件。
     *
     * @param clazz 组件类型
     * @return 如果没有就返回null
     */
    <T extends IComponent> T getComponent(Class<T> clazz);

    /**
     * 获取所有组件。
     * 具体的实现类可以自定义排序。
     *
     * @return 包含所有组件的集合
     */
    Collection<IComponent> getComponents();

    /**
     * 注册一个组件。
     *
     * @param component 被注册的组件
     */
    void register(IComponent component);

}
