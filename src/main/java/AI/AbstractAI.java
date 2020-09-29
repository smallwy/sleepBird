package AI;

public abstract class AbstractAI {

    private AIState state = AIState.Idle;

    public abstract void onIdle();

    public abstract void onAttack();

    public abstract void onBeAttack();

    public abstract void onEscape();

    public void doAIAction(long time) {
        try {
            switch (state) {
                case Idle:
                    onIdle();
                    break;
                case Attact:
                    onAttack();
                    break;
                case BeAttack:
                    onBeAttack();
                    break;
                case Escape:
                    onEscape();
                    break;
                default:
                    onIdle();
            }
        } catch (Exception e) {
        }
    }
}
