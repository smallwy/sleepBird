package moster.infras.core.ecs;

import moster.infras.util.type.ManagedType;

/**
 * @author zhangfei
 */
@ManagedType
public interface IComponent {

    /**
     * 组件执行加载操作。
     *
     * @param ctx 提供给加载逻辑的上下文对象
     * @return 如果加载成功就返回true，否则返回false
     */
    default boolean load(Object ctx) {
        return true;
    }

}
