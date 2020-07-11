package moster.game.decision;

import moster.infras.util.type.TypeRegistry;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 决策事件管理器。
 *
 * @author zhangfei
 */
public class DecisionEventManager {

    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    /**
     * 所有的事件处理方法，R为决策类，C为事件处理方法的参数类型，V为事件处理方法。
     */
    private static final Table<Class<? extends ActionDecision>, Class<?>, Method> eventHandlerTable = HashBasedTable.create();

    public static void emit(ActionDecision decision, Object event) {
        Method method = DecisionEventManager.eventHandlerTable.get(decision.getClass(), event.getClass());
        if (method == null) {
            logger.error("决策类[{}]没有处理事件[{}]的方法", decision.getClass().getName(), event.getClass().getName());
            return;
        }

        try {
            method.invoke(decision, event);
        } catch (Exception e) {
            logger.error("决策类[{}]处理事件[{}]时出错", decision.getClass().getName(), event.getClass().getName(), e);
        }
    }

    /**
     * 决策事件管理器进行初始化。
     */
    public static boolean init() {
        List<Class<? extends ActionDecision>> classList = TypeRegistry.getSubTypes(ActionDecision.class);

        // 扫描所有处理决策事件的方法。
        for (Class<? extends ActionDecision> clazz : classList) {
            boolean success = scanEventMethods(clazz);
            if (!success) {
                return false;
            }
        }

        return true;
    }

    /**
     * 扫描决策类上的事件处理方法。
     */
    private static boolean scanEventMethods(Class<? extends ActionDecision> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            DecisionEvent event = method.getAnnotation(DecisionEvent.class);
            if (event == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                logger.error("决策[{}]的事件处理方法[{}]不符规则，参数数目不匹配", clazz.getName(), method.getName());
                return false;
            }

            DecisionEventManager.eventHandlerTable.put(clazz, parameterTypes[0], method);
        }

        return true;
    }

}
