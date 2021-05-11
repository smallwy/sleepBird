package asynDb;


public interface Synchronizer<T> {

    boolean update(T object);

    boolean delete(T object);

    boolean insert(T object);
}
