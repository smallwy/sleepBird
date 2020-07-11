package moster.game.common;

import moster.game.player.PlayerComponent;
import com.google.protobuf.AbstractMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共的数据管理
 */

@Component
public class CommonComponent extends PlayerComponent {
    private Map<Integer, AbstractMessage> respPortoMap = new ConcurrentHashMap<>();
    private int index = 0;
    private boolean nextExec;

    public <T extends AbstractMessage> void addResp(int commandId, T proto){
        respPortoMap.put(commandId, proto);
    }

    public <T extends AbstractMessage> T getResp(MessageType type) {
        return (T) respPortoMap.get(type.getCommandId());
    }

    public void clear(){
        this.respPortoMap.clear();
        this.index = 0;
        this.nextExec = false;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isNextExec() {
        return nextExec;
    }

    public void setNextExec(boolean nextExec) {
        this.nextExec = nextExec;
    }
}
