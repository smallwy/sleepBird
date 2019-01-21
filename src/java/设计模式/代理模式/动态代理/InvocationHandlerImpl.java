package 设计模式.代理模式.动态代理;

import 设计模式.代理模式.静态代理.IUserDao;
import 设计模式.代理模式.静态代理.UserImp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * 动态代理
 * @author wy
 * @create 2019-01-21 14:16
 **/
public class InvocationHandlerImpl implements InvocationHandler {
    private Object target;

    public InvocationHandlerImpl(Object target){
        this.target=target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;
        System.out.println("开始执行");
        result= method.invoke(target,args);
        System.out.println("结束执行");
        return result;
    }
    public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 被代理对象
        IUserDao userDao = new UserImp();
        InvocationHandlerImpl invocationHandlerImpl = new InvocationHandlerImpl(userDao);
        ClassLoader loader = userDao.getClass().getClassLoader();
        Class<?>[] interfaces = userDao.getClass().getInterfaces();
        // 主要装载器、一组接口及调用处理动态代理实例
        IUserDao newProxyInstance = (IUserDao) Proxy.newProxyInstance(loader, interfaces, invocationHandlerImpl);
        newProxyInstance.save();
    }

}
