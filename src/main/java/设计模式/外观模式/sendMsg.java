package 设计模式.外观模式;

/**
 * @author wy
 * @create 2019-01-21 16:17
 **/
public class sendMsg {
    public EmialService emialService;
    public WeiXinService weiXinService;
    public MSNService msnService;

    public sendMsg(){
     this.emialService=new emailImp();
     this. weiXinService=new weixinImp();
     this.msnService=new msnImp();
     }
    public void send() {
        emialService.sendEmial();
        weiXinService.sendWeinxin();
        msnService.sendMSN();
    }
}
