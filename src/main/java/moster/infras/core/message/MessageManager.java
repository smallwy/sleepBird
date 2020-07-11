package moster.infras.core.message;

import moster.game.common.MessageType;
import moster.infras.core.context.ServerContext;
import moster.infras.util.type.TypeRegistry;
import com.google.protobuf.AbstractMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息管理器，负责网络消息的注册、获取等。
 *
 * @author zhangfei
 */
public class MessageManager {

    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

    /**
     * 存放commandId与消息类型的映射关系。
     */
    private static final Map<Integer, Class<? extends AbstractMessage>> idToClassMap = new ConcurrentHashMap<>();

    /**
     * 存放消息类型与commandId的映射关系。
     */
    private static final Map<Class<? extends AbstractMessage>, Integer> classToIdMap = new ConcurrentHashMap<>();

    /**
     * 存放commandId与消息处理类的映射关系。
     */
    private static final Map<Integer, CommandHandler> commandHandlers = new ConcurrentHashMap<>();

    /**
     * 初始化。
     *
     * @return 如果初始化执行成功就返回true，否则返回false
     */
    @SuppressWarnings("unchecked")
    public static boolean init() {
        // 对消息处理类相关信息进行汇总。
        List<Class<? extends RegisteredMessageCommand>> list = TypeRegistry.getSubTypes(RegisteredMessageCommand.class);
        for (Class<? extends RegisteredMessageCommand> commandClass : list) {
            // 这里强制要求commandClass是一个spring bean。
            // 在消息处理类中，有很多依赖的服务也是通过spring注入进来的。
            RegisteredMessageCommand commandInstance = ServerContext.getBeanIfPresent(commandClass);
            if (commandInstance == null) {
                logger.error("消息指令类[{}]没有被spring管理", commandClass.getName());
                return false;
            }

            commandInstance.registerOutboundMessages();

            boolean success = commandInstance.registerInboundMethod();
            if (!success) {
                return false;
            }
        }

        // 根据所有的入站方法，建立一些映射关系。
        for (Map.Entry<Integer, Method> entry : RegisteredMessageCommand.inboundMethods.entrySet()) {
            Method method = entry.getValue();
            Class<? extends AbstractMessage> messageClass = (Class<? extends AbstractMessage>) method.getParameterTypes()[1];
            if(messageClass == null || messageClass == AbstractMessage.class)
                messageClass =  MessageType.valueof(entry.getKey());

            Integer commandId = entry.getKey();
            if (MessageManager.idToClassMap.containsKey(commandId)) {
                logger.error("commandId[{}]映射消息类型[{}]时出现重复[{}]", commandId, messageClass.getName(), idToClassMap.get(commandId).getName());
                return false;
            } else {
                MessageManager.idToClassMap.put(commandId, messageClass);
            }

            if (MessageManager.classToIdMap.containsKey(messageClass)) {
                logger.error("消息类型[{}]映射commandId[{}]时出现重复[{}]", messageClass.getName(), commandId, classToIdMap.get(messageClass));
                return false;
            } else {
                MessageManager.classToIdMap.put(messageClass, commandId);
            }

            Class<? extends RegisteredMessageCommand> commandClass = (Class<? extends RegisteredMessageCommand>) method.getDeclaringClass();
            RegisteredMessageCommand commandInstance = ServerContext.getBean(commandClass);

            String commandDesc = RegisteredMessageCommand.commandDescMap.get(commandId);
            CommandHandler handler = new CommandHandler(commandInstance, commandId, commandDesc, method);
            MessageManager.commandHandlers.put(commandId, handler);
        }

        // 根据所有的出站消息，建立一些映射关系。
        for (Map.Entry<Class<? extends AbstractMessage>, Integer> entry : RegisteredMessageCommand.outboundMessages.entrySet()) {
            Integer commandId = entry.getValue();
            Class<? extends AbstractMessage> messageClass = entry.getKey();
            MessageManager.idToClassMap.put(commandId, messageClass);
            MessageManager.classToIdMap.put(messageClass, commandId);
        }

        return true;
    }

    /**
     * 获取消息类型对应的commandId。
     *
     * @param clazz 消息类型
     * @return 如果没有找到就返回-1
     */
    public static int getCommandId(Class<? extends AbstractMessage> clazz) {
        return MessageManager.classToIdMap.getOrDefault(clazz, -1);
    }

    /**
     * 获取某个消息的描述。
     *
     * @param commandId 消息id
     * @return 对应的消息描述
     */
    public static String getCommandDesc(Integer commandId) {
        String commandDesc = RegisteredMessageCommand.commandDescMap.get(commandId);

        if (commandDesc != null) {
            return commandDesc;
        } else {
            return "未知";
        }
    }

    /**
     * 获取proto解析对象class
     * @param commandId
     * @return
     */
    public static Class<? extends AbstractMessage> getMessage(int commandId) {
        return MessageManager.idToClassMap.get(commandId);
    }

    /**
     * 获取相应的消息处理类。
     *
     * @param commandId 消息的commandId
     * @return 如果没有找到就返回null
     */
    public static CommandHandler getMessageHandler(Integer commandId) {
        return MessageManager.commandHandlers.get(commandId);
    }

}
