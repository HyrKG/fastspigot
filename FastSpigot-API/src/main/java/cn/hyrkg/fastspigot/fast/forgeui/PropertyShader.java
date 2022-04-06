package cn.hyrkg.fastspigot.fast.forgeui;

import cn.hyrkg.fastspigot.fast.forgeui.old.JsonProperty;

public class PropertyShader {
    public final SharedProperty property;

    public PropertyShader(SharedProperty property) {
        this.property = property;
    }

    public SharedProperty getProperty() {
        return property;
    }

    public JsonContent content(String key) {
        return new JsonContent(property, key);
    }
}
