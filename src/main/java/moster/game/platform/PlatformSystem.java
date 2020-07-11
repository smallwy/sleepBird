package moster.game.platform;

import moster.app.SimulatorContext;
import moster.infras.core.ecs.ISystem;
import moster.infras.util.JsonUtil;
import moster.infras.util.SHA1;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangfei
 */
@Component
public class PlatformSystem implements ISystem {

    private static final String SECRET_KEY = "75FA51CA64E917B0D1E82E44EBC0A789B7BC6C06";

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    private CloseableHttpAsyncClient httpClient;

    private String signInUrl;

    private String signUpUrl;

    /**
     * 平台账号登入操作。
     *
     * 向平台发起http请求，进行账号验证。
     * http请求的url类似：
     * http://127.0.0.1:9888/management/account/verify?accountName=jack&password=lei&sign=6681D947A3C76253B2F285E5247BC01F833852C8
     */
    public void signIn(PlatformComponent component) {
        String username = component.getUsername();
        String password = component.getPassword();

        // 需要根据参数和特定规则算出一个签名字符串，用于平台校验。
        String param = "accountName=" + username + "|password=" + password;
        String sign = SHA1.encode(param + SECRET_KEY).toUpperCase();

        String url = this.signInUrl + "accountName=" + username + "&password=" + password + "&sign=" + sign;
        SimpleHttpRequest request = SimpleHttpRequests.post(url);

        FutureCallback<SimpleHttpResponse> callback = createSignInCallback(component);
        this.httpClient.execute(request, callback);
    }

    /**
     * 平台账号注册操作。
     *
     * 向平台发起http请求，进行账号注册。
     * http请求的url类似：
     * http://127.0.0.1:9888/management/account/register?accountName=jack&password=lei&sign=6681D947A3C76253B2F285E5247BC01F833852C8
     */
    public void signUp(PlatformComponent component) {
        String username = component.getUsername();
        String password = component.getPassword();

        // 需要根据参数和特定规则算出一个签名字符串，用于平台校验。
        String param = "accountName=" + username + "|password=" + password;
        String sign = SHA1.encode(param + SECRET_KEY).toUpperCase();

        String url = this.signUpUrl + "accountName=" + username + "&password=" + password + "&sign=" + sign;
        SimpleHttpRequest request = SimpleHttpRequests.post(url);

        FutureCallback<SimpleHttpResponse> callback = createSignUpCallback(component);
        this.httpClient.execute(request, callback);
    }

    /**
     * 创建一个用于处理账号登入的回调实例。
     */
    private FutureCallback<SimpleHttpResponse> createSignInCallback(PlatformComponent component) {
        String username = component.getUsername();
        String password = component.getPassword();

        return new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse simpleHttpResponse) {
                int httpStatusCode = simpleHttpResponse.getCode();
                if (httpStatusCode != HttpStatus.SC_OK) {
                    component.setSignInStatus(-1);
                    logger.warn("玩家[{}#{}]使用账号登入平台失败[{}]", username, password, httpStatusCode);
                }

                else {
                    // json字符串格式类似为：
                    // {"msg":"","code":0,"data":{"openid":"1","platformType":"qf"}}
                    String jsonStr = simpleHttpResponse.getBody().getBodyText();
                    if (StringUtils.isEmpty(jsonStr)) {
                        component.setSignInStatus(-1);
                        logger.warn("玩家[{}#{}]使用账号登入平台失败，没有响应内容", username, password);
                    }

                    else {
                        JsonObject jsonObject = JsonUtil.fromJson(jsonStr, JsonObject.class);
                        int code = jsonObject.get("code").getAsInt();

                        if (code == 0) {
                            logger.info("玩家[{}#{}]登入平台账号成功", username, password);
                            component.setSignInStatus(1);
                            String openId = jsonObject.getAsJsonObject("data").get("openid").getAsString();
                            component.setOpenId(openId);
                        }

                        else if (code == 602) {
                            // 账号不存在，需要注册。
                            component.setSignInStatus(-2);
                        }

                        else {
                            // 账号验证出错。
                            component.setSignInStatus(-1);
                            logger.error("玩家[{}#{}]使用账号登入平台失败，错误代码[{}]", username, password, code);
                        }
                    }
                }
            }

            @Override
            public void failed(Exception e) {
                component.setSignInStatus(-1);
                logger.error("玩家[{}#{}]使用账号登入平台失败", username, password, e);
            }

            @Override
            public void cancelled() {
                component.setSignInStatus(-1);
                logger.warn("玩家[{}#{}]使用账号登入平台失败，操作被取消", username, password);
            }
        };
    }

    /**
     * 创建一个用于处理账号注册的回调实例。
     */
    private FutureCallback<SimpleHttpResponse> createSignUpCallback(PlatformComponent component) {
        String username = component.getUsername();
        String password = component.getPassword();

        return new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse simpleHttpResponse) {
                int httpStatusCode = simpleHttpResponse.getCode();
                if (httpStatusCode != HttpStatus.SC_OK) {
                    component.setSignUpStatus(-1);
                    logger.warn("玩家[{}#{}]注册平台账号失败[{}]", username, password, httpStatusCode);
                }

                else {
                    // json字符串格式类似为：
                    // {"msg":"","code":0}
                    String jsonStr = simpleHttpResponse.getBody().getBodyText();
                    if (StringUtils.isEmpty(jsonStr)) {
                        component.setSignUpStatus(-1);
                        logger.warn("玩家[{}#{}]注册平台账号失败，没有响应内容", username, password);
                    }

                    else {
                        JsonObject jsonObject = JsonUtil.fromJson(jsonStr, JsonObject.class);
                        int code = jsonObject.get("code").getAsInt();

                        if (code == 0) {
                            logger.warn("玩家[{}#{}]注册平台账号成功", username, password);
                            component.setSignUpStatus(1);
                        }

                        else {
                            // 账号注册出错。
                            component.setSignUpStatus(-1);
                            logger.error("玩家[{}#{}]注册平台账号失败，错误代码[{}]", username, password, code);
                        }
                    }
                }
            }

            @Override
            public void failed(Exception e) {
                component.setSignInStatus(-1);
                logger.error("玩家[{}#{}]注册平台账号失败", username, password, e);
            }

            @Override
            public void cancelled() {
                component.setSignInStatus(-1);
                logger.warn("玩家[{}#{}]注册平台账号失败，操作被取消", username, password);
            }
        };
    }

    @Override
    public boolean init() {
        this.httpClient = HttpAsyncClients.createDefault();
        httpClient.start();

        String platformUrl = SimulatorContext.getGlobalConfig().getPlatformUrl();
        this.signInUrl = platformUrl + "management/account/verify?";
        this.signUpUrl = platformUrl + "management/account/register?";
        return true;
    }

}
