package asyndb;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class AsynDBEntity {
    private static final AtomicReferenceFieldUpdater<AsynDBEntity, AsynDBState> stateUpdater = AtomicReferenceFieldUpdater.newUpdater(AsynDBEntity.class, AsynDBState.class, "state");
    private transient AsynDBState state = AsynDBState.NORMAL;
    private transient Synchronizer synchronizer;
    private transient SyncQueue syncQueue;

    public boolean submit(Operation operation) {
        AsynDBState currentState = null;
        for (; ; ) {
            currentState = state;
            if (operation.isCanOperation(currentState)) {
                if (operation.isNeedChange(currentState)) {
                    if (!stateUpdater.compareAndSet(this, currentState, operation.STATE)) {
                        continue;
                    }
                }
                return currentState == AsynDBState.NORMAL;
            }
            throw new RuntimeException("[" + this + "]" + "submit exception " + currentState + " " + operation);
        }
    }

    public boolean trySync(int maxTime) {
        int tryCount = 0;
        for (; ; ) {
            AsynDBState asynDBState = state;
            if (stateUpdater.compareAndSet(this, asynDBState, asynDBState != AsynDBState.DELETE ? AsynDBState.NORMAL : AsynDBState.DELETEED)) {
                while (tryCount++ < maxTime) {
                    if (asynDBState.doOperation(synchronizer, this)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public Synchronizer getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    public int getHash() {
        return hashCode();
    }

    public AsynDBState getState() {
        return state;
    }

    public void setState(AsynDBState state) {
        this.state = state;
    }

    public SyncQueue getSyncQueue() {
        return syncQueue;
    }

    public void setSyncQueue(SyncQueue syncQueue) {
        this.syncQueue = syncQueue;
    }

    public void serialize(){

    }
}
