package asynDb;

public class SyncStats {
    private int wating;

    private long total;

    private long periodNum;

    public SyncStats(int wating, long total, long periodNum) {
        super();
        this.wating = wating;
        this.total = total;
        this.periodNum = periodNum;
    }

    public int getWating() {
        return wating;
    }

    public void setWating(int wating) {
        this.wating = wating;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPeriodNum() {
        return periodNum;
    }

    public void setPeriodNum(long periodNum) {
        this.periodNum = periodNum;
    }
}
