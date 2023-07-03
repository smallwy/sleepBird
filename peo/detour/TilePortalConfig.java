package detour;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class TilePortalConfig {
    public int id;
    public String mapPath;
    public TilePortalDetail[] portalDetails;

    public TilePortalConfig(int id,String mapPath){
        this.id = id;
        this.mapPath = mapPath;
        portalDetails = new TilePortalDetail[6];
        loadConfig();
    }

    private void loadConfig(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = TilePortalConfig.class.getClassLoader().getResourceAsStream(mapPath);
            Document doc = builder.parse(is);
            NodeList region = doc.getElementsByTagName("Region");
            if(region != null){
                loadRegion(region);
            }
            NodeList portal = doc.getElementsByTagName("Portal");
            if(portal != null){
                loadPortal(portal);
            }
        }catch (Exception e) {
            e.printStackTrace();
            //GlobalLogger.getInstance().error("load map fail by path ["+mapPath+"] !!!");
            System.out.println("load map fail by path ["+mapPath+"] !!!");
        }

    }

    public void loadRegion(NodeList region){
        for(int i=0;i<region.getLength();i++){
            NamedNodeMap map = region.item(i).getAttributes();

            String type = map.getNamedItem("type").getNodeValue();
            String x = map.getNamedItem("x").getNodeValue();
            String z = map.getNamedItem("z").getNodeValue();
            String width = map.getNamedItem("width").getNodeValue();
            String height = map.getNamedItem("height").getNodeValue();
        }
    }

    public void loadPortal(NodeList portal){
        for(int i=0;i<portal.getLength();i++){
            NamedNodeMap map = portal.item(i).getAttributes();
            if(map.getNamedItem("type") != null){
                String id = map.getNamedItem("id").getNodeValue();
                String type = map.getNamedItem("type").getNodeValue();
                float x = Float.parseFloat(map.getNamedItem("x").getNodeValue());
                float z = Float.parseFloat(map.getNamedItem("z").getNodeValue());

                if("enter".equals(type)){
                    if(x != 0f || z != 0f){
                       // GlobalLogger.getInstance().error("the enter position must be [0,0],but there is ["+x+","+z+"] in file["+mapPath+"] !!!");
                    }
                }

                portalDetails[Integer.parseInt(id)-1] = new TilePortalDetail(Integer.parseInt(id),type,(int)(x*100),(int)(z*100));
            }
        }
    }

}

