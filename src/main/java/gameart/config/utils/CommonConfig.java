/*
package gameart.config.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gameart.utils.type.annotation.TypeManagered;

import java.net.URL;
import java.util.Properties;

@TypeManagered
public class CommonConfig {
	public void setValue() throws Exception {
		URL resource = Thread.currentThread().getContextClassLoader().getResource("config/" + this.getClass().getSimpleName() + ".json");
		JSONObject jsonObject = JSON.parseObject(Json2BeanUtils.getJsonStr(resource.getPath()));
		JSONArray jsonArray = jsonObject.getJSONArray("array");
		final Properties properties = new Properties();
		jsonArray.forEach(jo1 -> {
			JSONObject jo = (JSONObject) jo1;
			String key = jo.getString("Id");
			String value = jo.getString("ConfigValue");
			if (key == null)
				key = jo.getString("id");
			if (value == null)
				value = jo.getString("configValue");
			properties.setProperty(key, value);
		});
		ConfigUtils.autowired(properties, this.getClass());
	}
}
*/
