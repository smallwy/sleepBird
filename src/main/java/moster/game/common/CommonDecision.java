package moster.game.common;

import moster.game.common.handler.AbstractHandler;
import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import moster.infras.core.context.ServerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public abstract class CommonDecision extends ActionDecision {
    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * 执行的处理器列表
     */
    protected List<AbstractHandler> handlerList = new ArrayList<>();

    /**
     * 注册handler
     *
     * @param clazz
     */
    public void registerHandler(Class<? extends AbstractHandler> clazz) {
        this.handlerList.add(ServerContext.getApplicationContext().getBean(clazz));
    }

    @Override
    public void tick(long currTime) {
        if (this.handlerList.size() <= 0) {
            this.decisionSystem.switchNext(super.owner);
            return;
        }

        CommonComponent component = this.owner.getComponent(CommonComponent.class);
        AbstractHandler handler = handlerList.get(component.getIndex());
        if (component.isNextExec()) {
            handler.nextFrameExecute(owner);
            component.setNextExec(false);
            component.setIndex(component.getIndex() + 1);

            if (component.getIndex() == handlerList.size()) {
                this.decisionSystem.switchNext(super.owner);
                return;
            }
            //执行下一个
            handler = handlerList.get(component.getIndex());
            execute(component, handler);
        } else {
            execute(component, handler);
        }
    }

    private void execute( CommonComponent component, AbstractHandler handler){
        handler.execute(this.owner);
        if (handler.hasNextExecute()) {
            component.setNextExec(true);
        }else{
            component.setIndex(component.getIndex() + 1);
            if (component.getIndex() == handlerList.size()) {
                this.decisionSystem.switchNext(super.owner);
            }
        }
    }

    @Override
    public void onEnter() {
        this.owner.getComponent(CommonComponent.class).clear();
    }
}
