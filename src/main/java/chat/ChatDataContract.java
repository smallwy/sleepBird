package chat;

public class ChatDataContract {

    public String content = ""; //发送内容

    public long sendId;  //发送者Id

    public String sendName = ""; //发送者Name

    public long receviceId; //接受者

    public String recName = ""; //接受着名字

    private ChatChannel chatChannel;  //消息频道

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSendId() {
        return sendId;
    }

    public void setSendId(long sendId) {
        this.sendId = sendId;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public long getReceviceId() {
        return receviceId;
    }

    public void setReceviceId(long receviceId) {
        this.receviceId = receviceId;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(ChatChannel chatChannel) {
        this.chatChannel = chatChannel;
    }
}
