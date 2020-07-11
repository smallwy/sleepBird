package moster.infras.core.message;

import moster.infras.util.type.ManagedType;
import com.google.protobuf.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangfei
 */
@ManagedType
public interface RegisteredMessageCommand {

    Logger logger = LoggerFactory.getLogger(RegisteredMessageCommand.class);

    /**
     * 用于处理入站消息的方法，key为commandId，value为对应的处理方法。
     */
    Map<Integer, Method> inboundMethods = new HashMap<>();

    /**
     * 出站消息，key为消息类型，value为commandId。
     */
    Map<Class<? extends AbstractMessage>, Integer> outboundMessages = new HashMap<>();

    /**
     * 所有的消息描述，key消息id，value为消息描述。
     */
    Map<Integer, String> commandDescMap = new HashMap<>();

    /**
     * 注册用于处理入站消息的方法。
     */
    default boolean registerInboundMethod() {
        Class<? extends RegisteredMessageCommand> commandClass = getClass();

        for (Method method : commandClass.getDeclaredMethods()) {
            InboundMessageCommand command = method.getAnnotation(InboundMessageCommand.class);
            if (command == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 2) {
                logger.error("消息处理类[{}]的方法[{}]不符规则，参数数目不匹配", commandClass.getName(), method.getName());
                return false;
            }

            if (!AbstractMessage.class.isAssignableFrom(parameterTypes[1])) {
                logger.error("消息处理类[{}]的方法[{}]不符规则，第二个参数不是消息类型", commandClass.getName(), method.getName());
                return false;
            }

            Integer commandId = command.commandId();
            Method existMethod = RegisteredMessageCommand.inboundMethods.get(commandId);
            if (existMethod != null) {
                logger.error("消息处理类[{}]的方法[{}]映射commandId[{}]出现重复[{}]",
                        commandClass.getName(), method.getName(), commandId, existMethod.getName());
                return false;
            }

            if (RegisteredMessageCommand.commandDescMap.containsKey(commandId)) {
                logger.error("消息处理类[{}]的方法[{}]映射commandId[{}]出现重复[{}]",
                        commandClass.getName(), method.getName(), commandId, this.commandDescMap.get(commandId));
                return false;
            }

            RegisteredMessageCommand.commandDescMap.put(commandId, command.desc());
            RegisteredMessageCommand.inboundMethods.put(commandId, method);
        }

        return true;
    }

    /**
     * 注册出站消息类型与commandId的映射关系。
     */
    default void registerOutboundMessages() {

    }

    /**
     * 注册出站消息类型与消息id的映射关系。
     *
     * @param protoClass 消息类型
     * @param commandId 消息id
     */
    static void registerOutboundMessage(Class<? extends AbstractMessage> protoClass, int commandId, String commandDesc) {
        if (RegisteredMessageCommand.commandDescMap.containsKey(commandId)) {
            throw new IllegalArgumentException("出站消息[" + commandId + "]已经被注册为["
                    + RegisteredMessageCommand.commandDescMap.get(commandId) + "]");
        }

        RegisteredMessageCommand.commandDescMap.put(commandId, commandDesc);
        RegisteredMessageCommand.outboundMessages.put(protoClass, commandId);
    }

}
