package moster.infras.core.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author zhangfei
 */
@Component
public class ServerContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        ServerContext.applicationContext = applicationContext;
    }

    /**
     * 执行初始化。
     *
     * @param configLocations spring配置文件所在位置
     */
    public static void init(String... configLocations) {
        ServerContext.applicationContext = new ClassPathXmlApplicationContext(configLocations);
    }

    /**
     * @return 持有的spring应用上下文
     */
    public static ApplicationContext getApplicationContext() {
        return ServerContext.applicationContext;
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @return 对应的bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) ServerContext.applicationContext.getBean(beanName);
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @return 对应的bean实例，如果没有就返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanIfPresent(String beanName) {
        try {
            return (T) ServerContext.applicationContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @param clazz    bean的类型
     * @return 对应的bean实例
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return ServerContext.applicationContext.getBean(beanName, clazz);
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @param clazz    bean的类型
     * @return 对应的bean实例，如果没有就返回null
     */
    public static <T> T getBeanIfPresent(String beanName, Class<T> clazz) {
        try {
            return ServerContext.applicationContext.getBean(beanName, clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 从容器中获取bean。
     *
     * @param clazz bean的类型
     * @return 对应的bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return ServerContext.applicationContext.getBean(clazz);
    }

    /**
     * 从容器中获取bean。
     *
     * @param clazz bean的类型
     * @return 对应的bean实例，如果没有就返回null
     */
    public static <T> T getBeanIfPresent(Class<T> clazz) {
        try {
            return ServerContext.applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @param params   参数
     * @return 对应的bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Object... params) {
        return (T) ServerContext.applicationContext.getBean(beanName, params);
    }

    /**
     * 从容器中获取bean。
     *
     * @param beanName bean的名称
     * @param params   参数
     * @return 对应的bean实例，如果没有就返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanIfPresent(String beanName, Object... params) {
        try {
            return (T) ServerContext.applicationContext.getBean(beanName, params);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 从容器中获取bean。
     *
     * @param params 参数
     * @param clazz bean的类型
     * @return 对应的bean实例
     */
    public static <T> T getBean(Class<T> clazz, Object... params) {
        return (T) ServerContext.applicationContext.getBean(clazz, params);
    }

    /**
     * 从容器中获取bean。
     *
     * @param params 参数
     * @param clazz bean的类型
     * @return 对应的bean实例，如果没有就返回null
     */
    public static <T> T getBeanIfPresent(Class<T> clazz, Object... params) {
        try {
            return (T) ServerContext.applicationContext.getBean(clazz, params);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * 获取所有被某个注解标注的bean。
     *
     * @param annotationClass 注解类型
     * @return 所有被这个注解标注的bean
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationClass) {
        return ServerContext.applicationContext.getBeansWithAnnotation(annotationClass);
    }

}
