package detour.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class TilePointData {

    private int[] startPos;
    private int[] counts;
    private int enterId;
    private int accuracy;
    private int cellSize;
    private boolean[][] datas;

    public TilePointData(){}

    public TilePointData(String startPos, String sizes,String enterId, String accuracy, String datas) {

        this.startPos = new int[2];
        String[] startPosStrs = startPos.split(",");
        this.startPos[0] = Integer.parseInt(startPosStrs[0])*100;
        this.startPos[1] = Integer.parseInt(startPosStrs[1])*100;

        String[] sizesStrs = sizes.split(",");
        int xLength = Integer.parseInt(sizesStrs[0])*100;
        int yLength = Integer.parseInt(sizesStrs[1])*100;

        this.enterId = Integer.parseInt(enterId);
        this.accuracy = Integer.parseInt(accuracy);
        this.cellSize = 100/this.accuracy;

        String[] dataStrs = datas.split(",");

        this.counts = new int[2];
        this.counts[0] = xLength*this.accuracy/100;
        this.counts[1] = yLength*this.accuracy/100;

        this.datas = new boolean[this.counts[0]][];

        int dataIndex = 0;
        long data = Long.parseLong(dataStrs[dataIndex]);
        int bitIndex = 0;

        int validPosCount = 0;
        for(int i=0;i<this.counts[0];i++){
            boolean[] zDatas = new boolean[this.counts[1]];
            for(int j=0;j<this.counts[1];j++){

                zDatas[j] = ((data & (1L << (63-bitIndex))) != 0);

                bitIndex ++;
                if(bitIndex == 64){
                    dataIndex ++;
                    data = Long.parseLong(dataStrs[dataIndex]);
                    bitIndex = 0;
                }

                //zDatas[j] = Integer.parseInt(dataStrs[i*this.counts[1] + j]) == 1;

                if(zDatas[j]){
                    validPosCount ++;
                }
            }
            this.datas[i] = zDatas;
        }

        System.out.println("create tile point data finish !!! valid pos count = "+validPosCount);

    }

    /**
     * 检测点是否在地图上
     * @param x
     * @param z
     * @return
     */
    public boolean isPointOnMap(int x,int z){

        int xIndex = (x-startPos[0])*accuracy/100;
        if(xIndex < 0 || xIndex >= this.counts[0]){
            return false;
        }

        int zIndex = (z-startPos[1])*accuracy/100;
        if(zIndex < 0 || zIndex >= this.counts[1]){
            return false;
        }

        return datas[xIndex][zIndex];

    }

    public int[] findCanUseNearestPosition(int x,int z){

        if(isPointOnMap(x,z)){
            return new int[]{x,z};
        }

        //设置搜索范围为半径为5m的圆
        int xIndex = (x-startPos[0])*accuracy/100;
        if(xIndex <= -500 || xIndex >= this.counts[0] + 500 ){
            return null;
        }

        int zIndex = (z-startPos[1])*accuracy/100;
        if(zIndex < -500 || zIndex >= this.counts[1] + 500){
            return null;
        }

        int cellLength = 1;
        for(int i=0;i<50;i++){
            for(int j=0;j<cellLength;j++){
                int testXIndex = xIndex+j;
                int testZIndex = zIndex+(cellLength-j);
                if(isPointOnMap(testXIndex,testZIndex)){
                    return new int[]{startPos[0]+testXIndex*cellSize,startPos[1]+testZIndex*cellSize,};
                }

                testXIndex = xIndex-j;
                testZIndex = zIndex+(cellLength-j);
                if(isPointOnMap(testXIndex,testZIndex)){
                    return new int[]{startPos[0]+testXIndex*cellSize,startPos[1]+testZIndex*cellSize,};
                }

                testXIndex = xIndex-j;
                testZIndex = zIndex-(cellLength-j);
                if(isPointOnMap(testXIndex,testZIndex)){
                    return new int[]{startPos[0]+testXIndex*cellSize,startPos[1]+testZIndex*cellSize,};
                }

                testXIndex = xIndex+j;
                testZIndex = zIndex-(cellLength-j);
                if(isPointOnMap(testXIndex,testZIndex)){
                    return new int[]{startPos[0]+testXIndex*cellSize,startPos[1]+testZIndex*cellSize,};
                }
            }
        }

        return null;
    }

    public int getEnterId() {
        return enterId;
    }

    public static void main(String[] args) throws Exception {
        String dataPath = "obj/4p_frenzyCamp_003_1.md";
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

        int posX = -425;
        int posZ = -67;
        boolean result = tilePointData.isPointOnMap(posX,posZ);
        if(result){
            System.out.println("point["+posX+","+posZ+"] is on map !!!");
        }else{
            System.out.println("point["+posX+","+posZ+"] is not on map !!!");
        }

    }
}
