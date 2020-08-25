package chat;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GuildMessageQueue extends AbstractChatMessageQueeue {

    private static final long GUILD = 0;

    private final ConcurrentHashMap<Long, Queue<ChatDataContract>> messageQueue = new ConcurrentHashMap<>();

    public GuildMessageQueue(ChatChannel chatChannel) {
        super(chatChannel);
    }

    @Override
    public boolean checkContidion(Object orgs) {
        return false;
    }

    @Override
    public void putMessageToQueue(ChatDataContract contract) {
        //TODO。。。。一些列判断处理
        long guild = 0;
        Queue<ChatDataContract> q = new ConcurrentLinkedDeque<>();
        messageQueue.putIfAbsent(guild, q);
        Queue<ChatDataContract> queue = messageQueue.get(guild);
        queue.add(contract);
    }

    public final class DuildChatMessageScheduleTask implements Runnable {
        @Override
        public void run() {
            if (!isEnable() || messageQueue.isEmpty()) {
                for (Map.Entry<Long, Queue<ChatDataContract>> entry : messageQueue.entrySet()) {
                    Queue<ChatDataContract> queue = entry.getValue();
                    Iterator<ChatDataContract> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        ChatDataContract dataContract = iterator.next();
                        iterator.remove();
                        //todo。。。send/....
                    }
                }
            }
        }
    }

}
