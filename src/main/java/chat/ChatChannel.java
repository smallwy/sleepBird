package chat;

public enum ChatChannel {
    WORLD(1, WorldMessageQueue.class),
    SYSTEM(2, null),
    DUILD(3, GuildMessageQueue.class);

    private int channelType;

    private Class<? extends AbstractChatMessageQueeue> handleClass;

    ChatChannel(int channelType, Class<? extends AbstractChatMessageQueeue> handleClass) {
        this.channelType = channelType;
        this.handleClass = handleClass;
    }

    public int getChannelType() {
        return channelType;
    }

    public Class<? extends AbstractChatMessageQueeue> getHandleClass() {
        return handleClass;
    }


    /*
     * 获取对应的频道channel
     * */
    public static ChatChannel getChanel(int channelType) {
        for (ChatChannel chatChannel : values()) {
            if (chatChannel.channelType == channelType) {
                return chatChannel;
            }
        }
        return null;
    }

}
