package asynDb;

@Persistent(syncClass = TestEntityManager.class)
public class TestEntity extends AsynDBEntity {
    private int id;
    private String name;
}
