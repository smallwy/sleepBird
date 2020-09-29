package gameart.manager.good;


import gameart.bean.good;
import gameart.config.ConfigManager;
import gameart.config.utils.Config;
import gameart.config.utils.IConfig;

import java.util.HashMap;
import java.util.Map;

@Config({good.class})
public class GoodCustom extends IConfig {
    private static Map<Integer, good> integergoodHashMap = new HashMap<>();

    @Override
    public void loadConf() {
        Map<Integer, good> integergoodHashMap = new HashMap<>();
        for (good ood : ConfigManager.getConfig(good.class).values()) {
            integergoodHashMap.put(ood.getId(), ood);
        }
        GoodCustom.integergoodHashMap = integergoodHashMap;
    }
}
