package asynDb;

public interface ISyncStrategy {
    int getSleepTime(int waitTime);

    int getNumEachLoop();

    int tryTimes();
}
