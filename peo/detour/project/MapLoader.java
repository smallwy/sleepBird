package detour.project;

import detour.*;
import org.recast4j.detour.tilecache.TileCache;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MapLoader {

    private static MapLoader instance;

    public String tileConfigFileName = "tile_config";

    /**
     * 地图数据集合 key = 地图ID  value = TileCache字节码
     */
    Map<Integer, byte[]> tileCacheMap;

    /**
     * mapData数据集合 key = 地图ID
     */
    Map<Integer, TilePointData> tilePointDataMap;

    /**
     * key = 地图ID
     */
    Map<Integer, TilePortalConfig> portalConfigMap;

    List<TilePortalConfig> portalConfigList;

    /**
     * key = 地图入口ID
     */
    Map<Integer, List<TilePortalConfig>> enterConfigMap;
    /**
     * key =地块唯一id  value =地块文件名
     */
    Map<Integer,String> idTileNames ;

    /**
     * 关卡配置文件
     */
    Map<Integer, MisBasicModel> idMisBasicModels ;

/*    *//**
     * 普通关卡地图信息
     *//*
    public GeneralLevelMapDictionary generalLevelMapDictionary;*/



    private MapLoader(){

        tileCacheMap = new HashMap<>();
        tilePointDataMap = new HashMap<>();

        portalConfigMap = new HashMap<>();
        portalConfigList = new ArrayList<>();
        enterConfigMap = new HashMap<>();

        idTileNames = new HashMap<>();

        idMisBasicModels = new HashMap<>();

      /*  generalLevelMapDictionary = new GeneralLevelMapDictionary();
        idleLevelMapDictionary = new IdleLevelMapDictionary();
        greaterRiftsMapDictionary = new GreaterRiftsMapDictionary();
        climbTowerMapDictionary = new ClimbTowerMapDictionary();
        arenaMapDictionary = new ArenaMapDictionary();
        jewelsMazeMapDictionary = new JewelsMazeMapDictionary();
        groupExplorationMapDictionary = new GroupExplorationMapDictionary();
        fieldTaskMapDictionary = new FieldTaskMapDictionary();
        practiceHallMapDictionary = new PracticeHallMapDictionary();
        worldBossMapDictionary = new WorldBossMapDictionary();
        sparkMapDictionary = new SparkMapDictionary();*/

    }

    public static MapLoader getInstance(){
        if(instance == null){
            instance = new MapLoader();
        }
        return instance;
    }

    public void loadMap(){
        loadTileIdAndName();
        loadLevelIdAndName();
        loadTileConfig();

      /*  generalLevelMapDictionary.loadLevelConfig(idMisBasicModels);*/

        System.out.println("load map and level config success !!!");
    }

    public void loadTileIdAndName(){
       /* Map<Integer, MisTileModel> tileModels = MisTileDictionary.getInstance().getMisTileModels();

        for(Integer tileId : tileModels.keySet()){
            idTileNames.put(tileId,tileModels.get(tileId).getTilePathServer());
        }*/
    }

    public void loadLevelIdAndName(){

     /*   List<MisBasicModel> models = MisBasicDictionary.getInstance().getAllMisBasicModel();

        for(MisBasicModel model : models){
            idMisBasicModels.put(model.getMisId(),model);
        }

        if(GameConstant.TEST_MAP_NAME != null && !GameConstant.TEST_MAP_NAME.trim().equals("")){
            MisBasicModel model = new MisBasicModel();
            model.setMisId(0);
            model.setMisMap(GameConstant.TEST_MAP_NAME);
            model.setIdleMisMap(GameConstant.TEST_MAP_NAME);
            model.setMonsterLevel(1);
            model.setMonsterDifficulty(1);
            idMisBasicModels.put(0,model);
        }*/
    }

    public void loadTileConfig(){

        Iterator<Integer> keys = idTileNames.keySet().iterator();
        while(keys.hasNext()){
            int mapId = keys.next();
            //System.out.println("load map "+idTileNames.get(mapId));

            try{

                createTileCache(mapId,"obj/"+idTileNames.get(mapId)+".obj");
                //createNavMesh(mapId,"obj/"+idTileNames.get(mapId)+".obj");
                TilePortalConfig portalConfig = new TilePortalConfig(mapId,"obj/"+idTileNames.get(mapId)+".xml");
                portalConfigMap.put(mapId,portalConfig);
                portalConfigList.add(portalConfig);
                for(TilePortalDetail portalDetail : portalConfig.portalDetails){
                    if(portalDetail != null && portalDetail.type.equals("enter")){
                        List<TilePortalConfig> detailList = enterConfigMap.get(portalDetail.id);
                        if(detailList == null){
                            detailList = new ArrayList<>();
                            enterConfigMap.put(portalDetail.id, detailList);
                        }
                        detailList.add(portalConfig);
                    }
                }
                //最后加载md文件，需要xml文件的入口信息
                parseTilePointData(mapId,"obj/"+idTileNames.get(mapId)+".md");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    private void createNavMesh(int mapId,String mapPath) {
//        try{
//            GameInputGeomProvider geom = new ObjImporter().load(ObjImporter.class.getClassLoader().getResourceAsStream(mapPath));
//            NavMesh navMesh = new NavMesh(new RecastMeshBuilder(geom).getMeshData(), 6, 0);
//            navMeshMap.put(mapId,navMesh);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private void parseTilePointData(int mapId,String dataPath) {
        try{
            URL url = MapLoader.class.getClassLoader().getResource(dataPath);
            if(url == null){
                return;
            }

            dataPath = MapLoader.class.getClassLoader().getResource(dataPath).getPath();
            File file = new File(dataPath);

            if(!file.exists()){
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String startPos = br.readLine();
            String sizes = br.readLine();
            String enterId = br.readLine();
            String accuracy = br.readLine();
            String datas = br.readLine();

            TilePointData tilePointData = new TilePointData(startPos,sizes,enterId,accuracy,datas);

//            if(getEnterIdById(mapId) != tilePointData.getEnterId()){
//                GlobalLogger.getInstance().error("the enter["+tilePointData.getEnterId()+"] in md file["+dataPath+"] is not match enter["+getEnterIdById(mapId)+"] in xml file !!!");
//            }

            int[] enterPos = getEnterPosById(mapId);
            if(!tilePointData.isPointOnMap(enterPos[0],enterPos[1])){
               // GlobalLogger.getInstance().error("maybe the enter point info from "+portalConfigMap.get(mapId).mapPath+" is wrong !!!");
                //GlobalLogger.getInstance().error("enter point["+enterPos[0]+","+enterPos[1]+"] is not on map from "+dataPath+" !!!");
            }

            tilePointDataMap.put(mapId,tilePointData);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public TilePointData getTilePointDataById(int mapId){
        return tilePointDataMap.get(mapId);
    }

    private void createTileCache(int mapId,String mapPath) {

        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        try{
            GameInputGeomProvider geom = new ObjImporter().load(ObjImporter.class.getClassLoader().getResourceAsStream(mapPath));
            TileCache tileCache = new NewRecastMeshBuilder(geom).getTileCache();

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            oos.writeObject(tileCache);

            tileCacheMap.put(mapId, baos.toByteArray());

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(oos != null){
                    oos.close();
                }
                if(oos != null){
                    baos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public TileCache getTileCacheById(int mapId){

        byte[] bytes = tileCacheMap.get(mapId);
        if(bytes == null){
            return null;
        }

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try{

            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);

            TileCache tileCache = (TileCache)ois.readObject();

            return tileCache;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(ois != null){
                    ois.close();
                }
                if(bais != null){
                    bais.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        return null;

    }

    public boolean hasObjectByTileId(int mapId){
        return tileCacheMap.get(mapId) != null;
    }

    public String getMapNameById(int mapId){
        return idTileNames.get(mapId);
    }

    public int[] getExitPosById(int tileId,byte exit){
        TilePortalConfig portalConfig = portalConfigMap.get(tileId);
        TilePortalDetail portalDetail = portalConfig.portalDetails[exit-1];
        return new int[]{portalDetail.x, portalDetail.z};
    }

    public int[] getEnterPosById(int tileId){
        TilePortalConfig portalConfig = portalConfigMap.get(tileId);
        for(TilePortalDetail portalDetail : portalConfig.portalDetails){
            if(portalDetail != null && portalDetail.type.equals("enter")){
                if(portalDetail.id == 1){ //上
                    return new int[]{0, -100};
                }
                if(portalDetail.id == 2){ //左
                    return new int[]{100, 0};
                }
                if(portalDetail.id == 3){ //下
                    return new int[]{0, 100};
                }
                if(portalDetail.id == 4){ //右
                    return new int[]{-100, 0};
                }
            }
        }
        return null;
    }

    public int getEnterIdById(int tileId){
        TilePortalConfig portalConfig = portalConfigMap.get(tileId);
        for(TilePortalDetail portalDetail : portalConfig.portalDetails){
            if(portalDetail != null && portalDetail.type.equals("enter")){
                return portalDetail.id;
            }
        }
        return 0;
    }

    public TilePortalConfig getTilePortalById(int tileId){
        return portalConfigMap.get(tileId);
    }

    public int getTileIdByPath(String path){
        Set<Integer> set = idTileNames.keySet();
        for(int key : set){
            if(path.endsWith(idTileNames.get(key))){
                return key;
            }
        }
        return 0;
    }

}

