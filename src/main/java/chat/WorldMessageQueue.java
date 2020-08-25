package chat;

public class WorldMessageQueue extends AbstractChatMessageQueeue {

    public WorldMessageQueue(ChatChannel chatChannel) {
        super(chatChannel);
    }

    @Override
    public boolean checkContidion(Object orgs) {
        return false;
    }

    @Override
    public void putMessageToQueue(ChatDataContract contract) {

    }
}
