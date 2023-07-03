package detour.project;

/**
 * 寻路处理类
 */


import org.recast4j.detour.crowd.Crowd;
import org.recast4j.detour.crowd.CrowdAgent;
import org.recast4j.detour.crowd.CrowdAgentParams;
import org.recast4j.detour.crowd.ObstacleAvoidanceQuery;
import org.recast4j.detour.crowd.debug.CrowdAgentDebugInfo;
import org.recast4j.detour.tilecache.TileCache;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.recast4j.detour.DetourCommon.*;

public class DetourHandler {
    public static void main(String[] args) {
        DetourHandler test = new DetourHandler(401001);
    }

    //不合格地址
    public static final int invalidPos = -1000;

    private final float[] m_polyPickExt = new float[] { 2, 10, 2 };

    protected detour.NavMeshQuery query;
    protected TileCache tileCache;
    protected detour.NavMesh navmesh;

    protected Crowd crowd;
    protected detour.QueryFilter filter;
    //网格对坐标点的查询范围
    private final float[] ext = new float[]{1,20,1};

    //Map<Integer, BattleBox> boxMap = new ConcurrentHashMap<>();

    /**
     * 向地图添加角色与运行地图角色寻路有冲突
     */
    private final ReentrantLock crowdLock = new ReentrantLock();

    public DetourHandler(int mapId){
        setUp(mapId);
    }

    private void setUp(int mapId) {

        tileCache = MapLoader.getInstance().getTileCacheById(mapId);

       // navmesh = tileCache.getNavMesh();

        //navmesh = MapLoader.getInstance().getNavMeshById(mapId);
        query = new detour.NavMeshQuery(navmesh);

       // crowd = new Crowd(GameConstant.MAX_DETOUR_AGENT*2, 0.6f, navmesh);

       // filter = crowd.getFilter(0);
        // Setup local avoidance params to different qualities.
        // Use mostly default settings, copy from dtCrowd.
        ObstacleAvoidanceQuery.ObstacleAvoidanceParams params = new ObstacleAvoidanceQuery.ObstacleAvoidanceParams(crowd.getObstacleAvoidanceParams(0));

        // Low (11)
        params.velBias = 0.5f;
        params.adaptiveDivs = 5;
        params.adaptiveRings = 2;
        params.adaptiveDepth = 1;
        crowd.setObstacleAvoidanceParams(0, params);

        // Medium (22)
        params.velBias = 0.5f;
        params.adaptiveDivs = 5;
        params.adaptiveRings = 2;
        params.adaptiveDepth = 2;
        crowd.setObstacleAvoidanceParams(1, params);

        // Good (45)
        params.velBias = 0.5f;
        params.adaptiveDivs = 7;
        params.adaptiveRings = 2;
        params.adaptiveDepth = 3;
        crowd.setObstacleAvoidanceParams(2, params);

        // High (66)
        params.velBias = 0.5f;
        params.adaptiveDivs = 7;
        params.adaptiveRings = 3;
        params.adaptiveDepth = 3;

        crowd.setObstacleAvoidanceParams(3, params);

    }

    public void release(){
        List<CrowdAgent> agents = crowd.getActiveAgents();
        for(CrowdAgent agent : agents){
            crowd.removeAgent(agent.idx);
        }

        tileCache = null;
        navmesh = null;
        query = null;
        crowd = null;
        filter = null;

        //roleMap.clear();
    }

    final detour.QueryFilter findStraightPathFilter = new detour.DefaultQueryFilter();
  /*  public void findStraightPath(BattleRole battleRole,int[] targetPos) {

        int[] currentPos = battleRole.getCurrentPosition();

        float[] startPos = new float[]{-currentPos[0]/100f, 0, currentPos[2]/100f};
        float[] endPos = new float[]{-targetPos[0]/100f, 0, targetPos[2]/100f};

        long startRef = query.findNearestPoly(startPos, m_polyPickExt, findStraightPathFilter).result.getNearestRef();
        long endRef = query.findNearestPoly(endPos, m_polyPickExt, findStraightPathFilter).result.getNearestRef();

        if(startRef == 0 || endRef == 0){
            battleRole.battleContainer.logger.error("findStraightPath error !!! startPos["+currentPos[0]+","+currentPos[2]+"] startRef["+startRef+"] endPos["+targetPos[0]+","+targetPos[2]+"] endRef["+endRef+"]");
            return;
        }

        detour.Result<List<Long>> path = query.findPath(startRef, endRef, startPos, endPos, findStraightPathFilter);

        detour.Result<List<detour.StraightPathItem>> result = query.findStraightPath(startPos, endPos, path.result,Integer.MAX_VALUE, 0);
        List<detour.StraightPathItem> straightPath = result.result;
        if(straightPath != null) {
            float[] resultPos = null;
            for (int j = 1; j < straightPath.size(); j++) {
                resultPos = straightPath.get(j).getPos();
//                if(j == straightPath.size()-1 && (resultPos[0] != endPos[0] || resultPos[2] != endPos[2])){
//                    resultPos[0] = endPos[0];
//                    resultPos[1] = endPos[1];
//                    resultPos[2] = endPos[2];
//                }

                if(j==1){
                    battleRole.proxy.setFindPathResult((int)(-resultPos[0]*100),(int)(resultPos[1]*100),(int)(resultPos[2]*100),true);
                }else{
                    battleRole.proxy.setFindPathResult((int)(-resultPos[0]*100),(int)(resultPos[1]*100),(int)(resultPos[2]*100),false);
                }

            }
        }else{
            System.err.println("straightPath == null !!!");
        }

    }*/

    public void testStraightPath(int[] currentPos,int[] targetPos) {

        float[] startPos = new float[]{-currentPos[0]/100f, 0, currentPos[2]/100f};
        float[] endPos = new float[]{-targetPos[0]/100f, 0, targetPos[2]/100f};

        long startRef = query.findNearestPoly(startPos, m_polyPickExt, findStraightPathFilter).result.getNearestRef();
        long endRef = query.findNearestPoly(endPos, m_polyPickExt, findStraightPathFilter).result.getNearestRef();

        if(startRef == 0 || endRef == 0){
            System.out.println("findStraightPath error !!! startPos["+startPos[0]+","+startPos[2]+"] startRef["+startRef+"] endPos["+endPos[0]+","+endPos[2]+"] endRef["+endRef+"]");
            return;
        }

        detour.Result<List<Long>> path = query.findPath(startRef, endRef, startPos, endPos, findStraightPathFilter);

        detour.Result<List<detour.StraightPathItem>> result = query.findStraightPath(startPos, endPos, path.result,Integer.MAX_VALUE, 0);
        List<detour.StraightPathItem> straightPath = result.result;
        if(straightPath != null) {
            if(straightPath.size() == 1){
                System.out.println("find path fail !!!");
            }
            float[] resultPos = null;
            for (int j = 1; j < straightPath.size(); j++) {
                resultPos = straightPath.get(j).getPos();

                System.out.println("resultPos"+j+" "+Arrays.toString(resultPos));
            }
        }else{
            System.err.println("straightPath == null !!!");
        }

    }

   // public final static float crowdTime = GameConstant.FRAME_TIME*GameConstant.PATH_CACHE_FRAME/1000f;
    /**
     * 用于多对象走向同一目标
     * 地图中障碍物过多时可能会出现堵死
     */
    public void findCrowdPath(){
        //long startTime = System.nanoTime();
        if(!crowdLock.isLocked()){
            try{
                crowdLock.lock();
                //设置角色速度
              //  for(Map.Entry<Integer, BattleRole> roleInfo  : roleMap.entrySet()){
                //    int idx = roleInfo.getKey();
                 //   BattleRole battleRole = roleInfo.getValue();
                    //CrowdAgent agent = crowd.getAgent(idx);

                    //System.err.println("["+battleRole.battleContainer.frameNum+"] modelId["+battleRole.getModelId()+"] start["+Arrays.toString(agent.npos)+"]");

                    int speed = 0;
                   // agent.vel[0] = 0;
                   // agent.vel[1] = 0;
                   // agent.vel[2] = 0;

                //    if(battleRole.proxy.isWaitingCrowdDetour()){
                  //      int[] targetPosition = battleRole.proxy.getCurrentMoveTargetPosition();
                   //     setAgentTarget(agent,targetPosition);
                   //     speed = battleRole.proxy.getMoveSpeed();

                    //    int dir = CalculationUtil.vectorToAngle(targetPosition[0]-battleRole.currentPosition[0],targetPosition[2]-battleRole.currentPosition[2]);

                        //agent.vel[0] = (float)(speed*Math.cos(dir*Math.PI/180)/100f);
                       // agent.vel[1] = 0;
                      //  agent.vel[2] = (float)(speed*Math.sin(dir*Math.PI/180)/100f);
//                        agent.vel[0] = agent.dvel[0];
//                        agent.vel[1] = 0;
//                        agent.vel[2] = agent.dvel[2];

                //    }

                 //   agent.params.maxSpeed = speed/100f;

             //   }
                //for(int i=0;i<GameConstant.PATH_CACHE_FRAME;i++){
            //    crowd.update(crowdTime, null);
             ///   for(CrowdAgent agent: crowd.getActiveAgents()){
               //     BattleRole battleRole = roleMap.get(agent.idx);
                 //   if(battleRole == null){
                //        continue;
                 //   }
                    //System.err.println("["+battleRole.battleContainer.frameNum+"] modelId["+battleRole.getModelId()+"] result["+Arrays.toString(agent.npos)+"]");

                  //  //System.out.println("["+battleRole.battleContainer.frameNum+"] modelId["+battleRole.getModelId()+"] corners size["+agent.corners.size()+"]");
                //    if(battleRole.proxy.isWaitingCrowdDetour()){
                     //   float resultX = -agent.npos[0];
                      //  float resultY = agent.npos[1];
                      //  float resultZ = agent.npos[2];
                  //      battleRole.proxy.setFindPathResult((int)(resultX*100),(int)(resultY*100),(int)(resultZ*100),true);

                  //  }
              //  }
                //}

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                crowdLock.unlock();
            }
        }
    }

    private final CrowdAgentDebugInfo agentDebug = new CrowdAgentDebugInfo();
    /**
     * 用于多对象走向同一目标
     * 地图中障碍物过多时可能会出现堵死
     */
  /*  public void findCrowdPathForOne(BattleRole battleRole){

        try{

            agentDebug.idx = battleRole.getCaIdx();

            CrowdAgent targetAgent = crowd.getAgent(agentDebug.idx);

            int[] targetPosition = battleRole.proxy.getCurrentMoveTargetPosition();
            setAgentTarget(targetAgent,targetPosition);

            int speed = battleRole.proxy.getMoveSpeed();
            targetAgent.params.maxSpeed = speed/100f;

            int dir = CalculationUtil.vectorToAngle(targetPosition[0]-battleRole.currentPosition[0],targetPosition[2]-battleRole.currentPosition[2]);

            targetAgent.vel[0] = (float)(speed*Math.cos(dir*Math.PI/180)/100f);
            targetAgent.vel[1] = 0;
            targetAgent.vel[2] = (float)(speed*Math.sin(dir*Math.PI/180)/100f);

            crowd.update(crowdTime, agentDebug);

            float resultX = -targetAgent.npos[0];
            float resultY = targetAgent.npos[1];
            float resultZ = targetAgent.npos[2];

            battleRole.proxy.setFindPathResult((int)(resultX*100),(int)(resultY*100),(int)(resultZ*100),true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    public void setAgentPosition(int idx,int[] currentPos){
        CrowdAgent ag = crowd.getAgent(idx);

        if(ag == null){
            throw new NullPointerException("can not find role in crowd by idx["+idx+"] !!!");
        }

        setAgentPosition(ag,currentPos);
    }

    private void setAgentPosition(CrowdAgent ag,int[] currentPos){
        detour.Result<detour.FindNearestPolyResult> nearest = query.findNearestPoly(new float[]{-currentPos[0]/100f,0,currentPos[2]/100f}, ext, filter);
        if(nearest.result.getNearestRef() != 0){
            float[] nearestPos = nearest.result.getNearestPos();

            ag.npos[0] = nearestPos[0];
            ag.npos[1] = nearestPos[1];
            ag.npos[2] = nearestPos[2];
        }else{
            ag.npos[0] = -(currentPos[0]/100f);
            ag.npos[1] = currentPos[1]/100f;
            ag.npos[2] = currentPos[2]/100f;
        }
    }

    public void setAgentTarget(int idx,int[] targetPos){

        CrowdAgent ag = crowd.getAgent(idx);

        setAgentTarget(ag,targetPos);
    }

    private void setAgentTarget(CrowdAgent ag,int[] targetPos){

        detour.Result<detour.FindNearestPolyResult> nearest = query.findNearestPoly(new float[]{-targetPos[0]/100f,targetPos[1]/100f,targetPos[2]/100f}, ext, filter);
        detour.Result<detour.FindNearestPolyResult> current = query.findNearestPoly(ag.npos, ext, filter);
        if (ag.isActive()) {
            if(ag.targetState == CrowdAgent.MoveRequestState.DT_CROWDAGENT_TARGET_NONE ||
                    ag.targetRef != nearest.result.getNearestRef() ||
                    current.result.getNearestRef() == nearest.result.getNearestRef()){
                crowd.requestMoveTarget(ag.idx, nearest.result.getNearestRef(), nearest.result.getNearestPos());
            }
        }
    }

/*    public int addAgentGrid(BattleRole battleRole, int updateFlags) {
        CrowdAgentParams ap = getAgentParams(battleRole, updateFlags);
        int[] startPos = battleRole.getCurrentPosition();
        detour.Result<detour.FindNearestPolyResult> nearest = query.findNearestPoly(new float[]{-startPos[0]/100f,0,startPos[2]/100f}, ext, filter);
        if(nearest.result.getNearestRef() != 0){
            float[] nearestPos = nearest.result.getNearestPos();
            int idx = -1;
            if(!crowdLock.isLocked()) {
                try {
                    crowdLock.lock();
                    idx = crowd.addAgent(nearestPos, ap);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    crowdLock.unlock();
                }
            }
            roleMap.put(idx,battleRole);
            return idx;
        }else{

            GlobalLogger.getInstance().error("battleRole startPos"+Arrays.toString(startPos));

            //addAgentGrid(battleRole,updateFlags);

            return -1;
        }

    }*/

  /*  public void removeAgentGrid(BattleRole battleRole){
        crowd.removeAgent(battleRole.getCaIdx());
        roleMap.remove(battleRole.getCaIdx());
    }*/

    protected CrowdAgentParams getAgentParams(float radius, int updateFlags, int obstacleAvoidanceType) {
        CrowdAgentParams ap = new CrowdAgentParams();
        ap.radius = radius;
        ap.height = 4f;
        ap.maxAcceleration = 8.0f;
        ap.maxSpeed = 10f;
        ap.collisionQueryRange = ap.radius * 12f;
        ap.pathOptimizationRange = ap.radius * 30f;
        ap.updateFlags = updateFlags;
        ap.obstacleAvoidanceType = obstacleAvoidanceType;
        ap.separationWeight = 2f;
        return ap;
    }

    /*protected CrowdAgentParams getAgentParams(BattleRole battleRole, int updateFlags) {
        CrowdAgentParams ap = new CrowdAgentParams();
        ap.radius = battleRole.getRadius()/100f;
        ap.height = 2;
        ap.maxAcceleration = 1000;
        ap.maxSpeed = 0;
        ap.collisionQueryRange = ap.radius * 12f;
        ap.pathOptimizationRange = ap.radius * 30f;
        ap.updateFlags = updateFlags;
        ap.obstacleAvoidanceType = 0;
        ap.separationWeight = 1f;
        return ap;
    }*/

    public long addObstacle(int[] pos,int radius,float height){
        long obstacleRef = tileCache.addObstacle(new float[]{-pos[0]/100f,pos[1]/100f,pos[2]/100f},radius/100f,height/100f);
        tileCache.update();
        return obstacleRef;
    }

 /*   public void removeObstacle(long obstacleRef){
        tileCache.removeObstacle(obstacleRef);
        tileCache.update();
    }*/

 /*   protected void setMoveTarget(float[] pos, boolean adjust) {
        float[] ext = crowd.getQueryExtents();
        detour.QueryFilter filter = crowd.getFilter(0);
        if (adjust) {
            for (int i = 0; i < crowd.getAgentCount(); i++) {
                CrowdAgent ag = crowd.getAgent(i);
                if (!ag.isActive()) {
                    continue;
                }
                float[] vel = calcVel(ag.npos, pos, ag.params.maxSpeed);
                crowd.requestMoveVelocity(i, vel);
            }
        } else {
            detour.Result<detour.FindNearestPolyResult> nearest = query.findNearestPoly(pos, ext, filter);
            for (int i = 0; i < crowd.getAgentCount(); i++) {
                CrowdAgent ag = crowd.getAgent(i);
                if (!ag.isActive()) {
                    continue;
                }
                crowd.requestMoveTarget(i, nearest.result.getNearestRef(), nearest.result.getNearestPos());
            }
        }
    }*/

    protected float[] calcVel(float[] pos, float[] tgt, float speed) {
        float[] vel = DetourCommon.vSub(tgt, pos);
        vel[1] = 0.0f;
        DetourCommon.vNormalize(vel);
        vel = DetourCommon.vScale(vel, speed);
        return vel;
    }
}
