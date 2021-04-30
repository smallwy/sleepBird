package battle;

public enum StatusType {
    baoji("暴击",1L<<0);

    private String desc;
    private long  status;

    StatusType(String desc, long status) {
        this.desc = desc;
        this.status = status;
    }

    public long getStatus() {
        return status;
    }
}
