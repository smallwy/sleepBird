package battle;

import java.util.List;

//战斗组
public class FightGroup {
    public static final FightGroup EMPTY=new FightGroup();

    static {

    }

    private long playerId;

    private String platform;

    private int serverId;

    private String name;

    private List<Fighter> fighters;

}

