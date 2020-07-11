package moster.game.decision.event;

/**
 * 关卡战斗返回的事件。
 *
 * @author zhangfei
 */
public class PassFightEvent {

    /**
     * 关卡id。
     */
    public final int passId;

    /**
     * 是否获胜。
     */
    public final boolean win;

    public PassFightEvent(int passId, boolean win) {
        this.passId = passId;
        this.win = win;
    }

}
