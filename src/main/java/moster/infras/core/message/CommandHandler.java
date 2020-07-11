package moster.infras.core.message;

import moster.infras.core.client.Client;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author zhangfei
 */
public class CommandHandler {

    private static Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    /**
     * 实际的消息处理方法。
     */
    private final Method method;

    /**
     * 消息处理方法关联的对象。
     * 消息处理方法是一个实例方法，被调用时需要传递实例对象。
     */
    private final RegisteredMessageCommand command;

    /**
     * 消息号。
     */
    private final int commandId;

    /**
     * 消息描述。
     */
    private final String commandDesc;

    /**
     * 用于将消息内容解析成proto message。
     */
    private Parser<? extends AbstractMessage> parser;

    /**
     * 处理消息。
     *
     * @param client 处理消息的client
     * @param content 消息内容
     */
    public void process(Client client, byte[] content) {
        AbstractMessage proto = null;

        try {
            proto = this.parser.parseFrom(content);
            logger.debug("[{}]收到消息({}-{})", client, this.commandId, this.commandDesc);
            method.invoke(this.command, client, proto);

        } catch (Throwable t) {
            if (proto == null) {
                logger.error("Client[{}]处理消息[{}-{}]时出现未知错误", client, this.commandId, this.commandDesc, t);

            } else {
                String jsonStr = null;
                try {
                    jsonStr = JsonFormat.printer().includingDefaultValueFields().print(proto);
                } catch (InvalidProtocolBufferException ex) {
                    // do nothing
                }
                logger.error("Client[{}]处理消息[{}-{}]时出现未知错误，消息内容[{}]",
                        client, this.commandId, this.commandDesc, jsonStr, t);
            }
        }
    }

    /**
     * 初始化用于解析消息内容的proto parser。
     */
    @SuppressWarnings("unchecked")
    private void initProtoParser() {
        Class<? extends AbstractMessage> protoClass = MessageManager.getMessage(commandId);
        try {
            Field field = protoClass.getDeclaredField("PARSER");
            field.setAccessible(true);
            this.parser = (Parser<? extends AbstractMessage>) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("获取proto的PARSER字段时出现异常", e);
        }
    }

    public CommandHandler(RegisteredMessageCommand command, int commandId, String commandDesc, Method method) {
        this.command = command;
        this.commandId = commandId;
        this.commandDesc = commandDesc;
        this.method = method;

        initProtoParser();
    }

}
