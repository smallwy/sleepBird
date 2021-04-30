package battle;

public interface IStatuable {

    boolean isStatus(StatusType type);

    boolean setStatus(StatusType type);

    boolean clearStatus(StatusType type);
}
