package asyndb;

import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsynDBService {

    private int threads = Math.min(16, Runtime.getRuntime().availableProcessors() * 2);

    private ISyncStrategy iSyncStrategy;

    private SyncQueuePool syncQueuePool;

    private final Map<Class<?>, Synchronizer> synchronizerMap = new ConcurrentHashMap();

    public void init(ApplicationContext applicationContext) {
        Map<String, Synchronizer> beansOfType = applicationContext.getBeansOfType(Synchronizer.class);
        for (Synchronizer value : beansOfType.values()) {
            Class<?>[] interfaces = value.getClass().getInterfaces();
            synchronizerMap.put(interfaces[0], value);
        }
    }

    @PostConstruct
    public void init0() {
        syncQueuePool = new SyncQueuePool(new SimpleThreadFactory("asynDb"), threads, iSyncStrategy);
    }

    public void delete(AsynDBEntity entity) {
        entity.serialize();
        this.doOperation(entity, Operation.DELETE);
    }

    public void update(AsynDBEntity entity) {
        entity.serialize();
        try {
            this.doOperation(entity, Operation.UPDATE);
        } catch (Exception e) {
            e.printStackTrace();//这里需要打印出来错误
        }
    }

    public void insert(AsynDBEntity entity) {
        entity.serialize();
        try {
            this.doOperation(entity, Operation.INSERT);
        } catch (Exception e) {
            e.printStackTrace();//这里需要打印出来错误
        }
    }

    private boolean doOperation(AsynDBEntity entity, Operation operation) {
        if (entity.submit(operation)) {
            if (entity.getSynchronizer() == null) {
                entity.setSynchronizer(this.synchronizerMap.get(getPersisClass(entity.getClass())));
            }
            return this.syncQueuePool.submit(entity);
        }
        return true;
    }

    private Class<?> getPersisClass(Class<?> entityClass) {
        Persistent persistent = entityClass.getAnnotation(Persistent.class);
        return persistent.syncClass();
    }

    public boolean stop() {
        try {
            return this.syncQueuePool.shutDown(10000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onServerClose() {
        this.stop();
    }

}
