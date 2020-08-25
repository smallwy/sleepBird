package chat;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TeamChatMessageQueue extends AbstractChatMessageQueeue {

    private static final int  teamId = 0;

    private final ConcurrentHashMap<Integer, Queue<ChatDataContract>> messageQueue = new ConcurrentHashMap<>();

    public TeamChatMessageQueue(ChatChannel chatChannel) {
        super(chatChannel);
    }


    @Override
    public boolean checkContidion(Object orgs) {
        return false;
    }

    @Override
    public void putMessageToQueue(ChatDataContract contract) {
        int teamId = 0;
        Queue<ChatDataContract> q = new ConcurrentLinkedDeque<>();
        messageQueue.putIfAbsent(teamId, q);
        Queue<ChatDataContract> queue = messageQueue.get(teamId);
        queue.add(contract);
    }


    public final class TeamChatMessageScheduleTask implements Runnable {
        @Override
        public void run() {
            if (!isEnable() || messageQueue.isEmpty()) {
                for (Map.Entry<Integer, Queue<ChatDataContract>> entry : messageQueue.entrySet()) {
                    int teamId=entry.getKey();
                    Queue<ChatDataContract> queue = entry.getValue();
                    if(!queue.isEmpty()){
                        doSendjob(new TeamChatMessageJob(teamId,queue));
                    }
                }
            }
        }
    }

    public final class TeamChatMessageJob implements Runnable {

        private int teamId;
        private Queue<ChatDataContract> queue;

        public int getTeamId() {
            return teamId;
        }

        public void setTeamId(int teamId) {
            this.teamId = teamId;
        }

        public Queue<ChatDataContract> getQueue() {
            return queue;
        }

        public void setQueue(Queue<ChatDataContract> queue) {
            this.queue = queue;
        }

        public TeamChatMessageJob(int teamId, Queue<ChatDataContract> queue) {
            this.teamId = teamId;
            this.queue = queue;
        }

        @Override
        public void run() {
            //send
        }
    }
}
