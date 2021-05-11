package asynDb;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public enum Operation {

    INSERT(AsynDBState.INSERT, new AsynDBState[]{AsynDBState.NORMAL}, null),

    DELETE(AsynDBState.DELETE, new AsynDBState[]{AsynDBState.NORMAL, AsynDBState.INSERT, AsynDBState.UPDATE}, new AsynDBState[]{AsynDBState.DELETE}),

    UPDATE(AsynDBState.UPDATE, new AsynDBState[]{AsynDBState.NORMAL},
            new AsynDBState[]{AsynDBState.UPDATE, AsynDBState.INSERT});

    public final AsynDBState STATE;
    private final Set<AsynDBState> NEED_CHANDE_OPERATION;
    private final Set<AsynDBState> CAN_OPERATION;

    private Operation(AsynDBState STATE, AsynDBState[] needChange, AsynDBState[] canOperation) {
        this.STATE = STATE;
        Set<AsynDBState> NEED_CHANDE_OPERATION = Sets.newHashSet();
        Set<AsynDBState> CAN_OPERATION = Sets.newHashSet();
        if (needChange != null) {
            for (AsynDBState asynDBState : needChange) {
                CAN_OPERATION.add(asynDBState);
                NEED_CHANDE_OPERATION.add(asynDBState);
            }
        }

        if (canOperation != null) {
            for (AsynDBState asynDBState : canOperation) {
                CAN_OPERATION.add(asynDBState);
            }
        }
        this.CAN_OPERATION = Collections.unmodifiableSet(CAN_OPERATION);
        this.NEED_CHANDE_OPERATION = Collections.unmodifiableSet(NEED_CHANDE_OPERATION);
    }

    public boolean isCanOperation(AsynDBState asynDBState) {
        return CAN_OPERATION.contains(asynDBState);
    }

    public boolean isNeedChange(AsynDBState asynDBState) {
        return NEED_CHANDE_OPERATION.contains(asynDBState);
    }
}
