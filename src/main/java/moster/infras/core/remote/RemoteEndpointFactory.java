package moster.infras.core.remote;

/**
 * @author zhangfei
 */
public interface RemoteEndpointFactory {

    RemoteEndpoint create(String host, int port);

}
