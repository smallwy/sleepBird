package moster.game.card;

import moster.game.common.CommonDecision;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class CardDecision extends CommonDecision {
    @PostConstruct
    public void initHandler() {
        this.registerHandler(RandomCardHandler.class);
    }
}
