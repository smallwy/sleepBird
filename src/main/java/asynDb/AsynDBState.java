package asynDb;

public enum AsynDBState {

    NORMAL() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            return true;
        }
    },

    DELETEED() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            return false;
        }
    },

    DELETE() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.delete(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("s%", asynDBEntity), e);
            }
        }
    },

    UPDATE() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.update(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("s%", asynDBEntity), e);
            }
        }
    },

    INSERT() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.insert(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("s%", asynDBEntity), e);
            }
        }
    };

    public abstract boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity);
}
