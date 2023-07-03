package detour;


import java.util.List;
import java.util.Map;

/**
 * @Description 关卡章节
 * @Author
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2023/05/29
*/
public class MisBasicModel
{
    private Integer misId;//关卡ID
    private Integer misType;//关卡类型
    private Integer misName;//关卡名称
    private List<List<Integer>> conditionType;//通关条件与显示
    private Integer preId;//上一关卡ID
    private Integer order;//关卡序列
    private Integer chapterId;//所属章节
    private Integer worldId;//所属地图
    private String misMap;//关联主线地图
    private Integer monsterLevel;//怪物等级
    private Integer monsterDifficulty;//怪物难度
    private List<List<Integer>> reward;//通关奖励
    private Integer enterLv;//进入等级
    private Integer enterComat;//进入战力
    private Integer waitTime;//进入等待时间（S）
    private String idleMisMap;//关联挂机地图
    private Integer idleKillMonster;//挂机怪物击杀要求
    private Integer idleMonsterLv;//挂机怪物等级
    private Map<Integer, Integer> difficultyCorrect;//属性难度修正
    private Integer battlefieldLv;//战地任务等级
    private Integer lightType;//光照
    private Integer isFinal;//是否主线最终关
    private Integer isGuide;//是否新手关
    private String startEvent;//关卡开始事件
    private String inforvalueUse;//关卡消耗情报值

    public Integer getMisId()
    {
        return this.misId;
    }

    public void setMisId(Integer misId)
    {
        this.misId = misId;
    }

    public Integer getMisType()
    {
        return this.misType;
    }

    public void setMisType(Integer misType)
    {
        this.misType = misType;
    }

    public Integer getMisName()
    {
        return this.misName;
    }

    public void setMisName(Integer misName)
    {
        this.misName = misName;
    }

    public List<List<Integer>> getConditionType()
    {
        return this.conditionType;
    }

    public void setConditionType(List<List<Integer>> conditionType)
    {
        this.conditionType = conditionType;
    }

    public Integer getPreId()
    {
        return this.preId;
    }

    public void setPreId(Integer preId)
    {
        this.preId = preId;
    }

    public Integer getOrder()
    {
        return this.order;
    }

    public void setOrder(Integer order)
    {
        this.order = order;
    }

    public Integer getChapterId()
    {
        return this.chapterId;
    }

    public void setChapterId(Integer chapterId)
    {
        this.chapterId = chapterId;
    }

    public Integer getWorldId()
    {
        return this.worldId;
    }

    public void setWorldId(Integer worldId)
    {
        this.worldId = worldId;
    }

    public String getMisMap()
    {
        return this.misMap;
    }

    public void setMisMap(String misMap)
    {
        this.misMap = misMap;
    }

    public Integer getMonsterLevel()
    {
        return this.monsterLevel;
    }

    public void setMonsterLevel(Integer monsterLevel)
    {
        this.monsterLevel = monsterLevel;
    }

    public Integer getMonsterDifficulty()
    {
        return this.monsterDifficulty;
    }

    public void setMonsterDifficulty(Integer monsterDifficulty)
    {
        this.monsterDifficulty = monsterDifficulty;
    }

    public List<List<Integer>> getReward()
    {
        return this.reward;
    }

    public void setReward(List<List<Integer>> reward)
    {
        this.reward = reward;
    }

    public Integer getEnterLv()
    {
        return this.enterLv;
    }

    public void setEnterLv(Integer enterLv)
    {
        this.enterLv = enterLv;
    }

    public Integer getEnterComat()
    {
        return this.enterComat;
    }

    public void setEnterComat(Integer enterComat)
    {
        this.enterComat = enterComat;
    }

    public Integer getWaitTime()
    {
        return this.waitTime;
    }

    public void setWaitTime(Integer waitTime)
    {
        this.waitTime = waitTime;
    }

    public String getIdleMisMap()
    {
        return this.idleMisMap;
    }

    public void setIdleMisMap(String idleMisMap)
    {
        this.idleMisMap = idleMisMap;
    }

    public Integer getIdleKillMonster()
    {
        return this.idleKillMonster;
    }

    public void setIdleKillMonster(Integer idleKillMonster)
    {
        this.idleKillMonster = idleKillMonster;
    }

    public Integer getIdleMonsterLv()
    {
        return this.idleMonsterLv;
    }

    public void setIdleMonsterLv(Integer idleMonsterLv)
    {
        this.idleMonsterLv = idleMonsterLv;
    }

    public Map<Integer, Integer> getDifficultyCorrect()
    {
        return this.difficultyCorrect;
    }

    public void setDifficultyCorrect(Map<Integer, Integer> difficultyCorrect)
    {
        this.difficultyCorrect = difficultyCorrect;
    }

    public Integer getBattlefieldLv()
    {
        return this.battlefieldLv;
    }

    public void setBattlefieldLv(Integer battlefieldLv)
    {
        this.battlefieldLv = battlefieldLv;
    }

    public Integer getLightType()
    {
        return this.lightType;
    }

    public void setLightType(Integer lightType)
    {
        this.lightType = lightType;
    }

    public Integer getIsFinal()
    {
        return this.isFinal;
    }

    public void setIsFinal(Integer isFinal)
    {
        this.isFinal = isFinal;
    }

    public Integer getIsGuide()
    {
        return this.isGuide;
    }

    public void setIsGuide(Integer isGuide)
    {
        this.isGuide = isGuide;
    }

    public String getStartEvent()
    {
        return this.startEvent;
    }

    public void setStartEvent(String startEvent)
    {
        this.startEvent = startEvent;
    }

    public String getInforvalueUse()
    {
        return this.inforvalueUse;
    }

    public void setInforvalueUse(String inforvalueUse)
    {
        this.inforvalueUse = inforvalueUse;
    }

    public Integer getKey()
    {
        return this.misId;
    }
}