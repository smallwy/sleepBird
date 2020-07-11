package moster.game.player;

import moster.infras.core.ecs.IComponent;

/**
 * 玩家组件。
 *
 * @author zhangfei
 */
public abstract class PlayerComponent implements IComponent {

    /**
     * 组件的拥有者。
     */
    protected Player owner;

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
