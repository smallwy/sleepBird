package moster.game.common.handler;

import moster.game.player.Player;

import java.lang.reflect.Method;

public abstract class AbstractHandler {
    public abstract void execute(Player owner);

    public void nextFrameExecute(Player owner) {
        System.out.println("AbstractHandler 下一个执行了");
    }

    public boolean hasNextExecute() {
        try {
            Method method = this.getClass().getMethod("nextFrameExecute", Player.class);
            Class<?> declaringClass = method.getDeclaringClass();
            return declaringClass != AbstractHandler.class;
        } catch (Exception e) {
            return false;
        }
    }
}
