package asyndb;

public class TestEntityManager implements Synchronizer<TestEntity> {
    @Override
    public boolean update(TestEntity object) {
        return false;
    }

    @Override
    public boolean delete(TestEntity object) {
        return false;
    }

    @Override
    public boolean insert(TestEntity object) {
        return false;
    }
}
