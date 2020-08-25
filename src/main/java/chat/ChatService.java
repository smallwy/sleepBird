package chat;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ChatService {

    //消息队列
    private Map<ChatChannel, AbstractChatMessageQueeue> MESSAGE_QUEUE = new HashMap<>();

    public void init() throws Exception {
        Map<ChatChannel, AbstractChatMessageQueeue> mq = new HashMap<>();
        for (ChatChannel value : ChatChannel.values()) {
            Class<? extends AbstractChatMessageQueeue> clazz = value.getHandleClass();
            if (clazz != null) {
                Constructor<? extends AbstractChatMessageQueeue> constructor = clazz.getConstructor(ChatChannel.class);
                AbstractChatMessageQueeue abstractChatMessageQueeue = constructor.newInstance(value);
                mq.put(value, abstractChatMessageQueeue);
            }
        }
        MESSAGE_QUEUE = mq;
    }


    public void sendChat(long playerId, ChatDataContract contract) {
        ChatChannel chatChannel = contract.getChatChannel();
        AbstractChatMessageQueeue abstractChatMessageQueeue = MESSAGE_QUEUE.get(chatChannel);
        //消息到来了  启动线程
        if(abstractChatMessageQueeue.isEnable()){
            abstractChatMessageQueeue.start();
        }

        abstractChatMessageQueeue.putMessageToQueue(contract);

    }
}
