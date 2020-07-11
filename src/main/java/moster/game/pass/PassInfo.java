package moster.game.pass;

/**
 * @author zhangfei
 */
public class PassInfo {

    /**
     * 关卡id。
     */
    int passId;

    /**
     * 是否通关。
     */
    boolean passed;

    /**
     * 通关星级。
     */
    int star;

    /**
     * 今天可挑战次数。
     */
    int fightNum;

    /**
     * 今天已购买次数。
     */
    int buyNum;

    /**
     * 首次通关时间。
     */
    long passTime;

}
