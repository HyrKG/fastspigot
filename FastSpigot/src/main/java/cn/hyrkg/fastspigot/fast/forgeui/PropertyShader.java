package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PropertyShader {
    public final SharedProperty property;

    protected HashMap<String, List<? extends PropertyShader>> arrayCacheMap = new HashMap<>();

    public PropertyShader(SharedProperty property) {
        this.property = property;
    }

    public SharedProperty getProperty() {
        return property;
    }


    public void clearCaches() {
        arrayCacheMap.clear();
    }


    public <T extends PropertyShader> List<T> getCachedPropertyArray(String key, Class<T> type) {
        if (!arrayCacheMap.containsKey(key)) {
            arrayCacheMap.put(key, getProperty().getArray(key, type));
        }
        return (List<T>) arrayCacheMap.get(key);
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
        return new JsonContent<Integer>(property, key, Integer.class);
    }

    public JsonContent<Boolean> cBool(String key) {
        return new JsonContent<Boolean>(property, key, Boolean.class);
    }


}
