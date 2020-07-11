package moster.infras.core.ecs;

import moster.infras.util.type.ManagedType;

/**
 * @author zhangfei
 */
@ManagedType
public interface ISystem {

    default boolean init() {
        return true;
    }

}
