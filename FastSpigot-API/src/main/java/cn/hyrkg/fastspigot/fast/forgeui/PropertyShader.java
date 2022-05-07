package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;

import java.util.UUID;

public class PropertyShader {
    public final SharedProperty property;

    public PropertyShader(SharedProperty property) {
        this.property = property;
    }

    public SharedProperty getProperty() {
        return property;
    }


    public JsonContent<String> cStr(String key) {
        return new JsonContent<String>(property, key, String.class);
    }

    public JsonContent<Double> cDouble(String key) {
        return new JsonContent<Double>(property, key, Double.class);
    }

    public JsonContent<Long> cLong(String key) {
        return new JsonContent<Long>(property, key, Long.class);
    }

    public JsonContent<Float> cFloat(String key) {
        return new JsonContent<Float>(property, key, Float.class);
    }

    public JsonContent<JsonArray> cJsonArray(String key) {
        return new JsonContent<JsonArray>(property, key, JsonArray.class);
    }

    public JsonContent<UUID> cUUID(String key) {
        return new JsonContent<UUID>(property, key, UUID.class);
    }

    public JsonContent<Integer> cInt(String key) {
        return new JsonContent<Integer>(property, key,Integer.class);
    }

}
