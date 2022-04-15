package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedProperty {
    private static SharedProperty EMPTY_PROPERTY = new SharedProperty();

    public SharedProperty(JsonObject jsonObject) {
        this.completeJson = jsonObject;
    }

    public SharedProperty() {
        this.completeJson = new JsonObject();
    }

    /**
     * ####################################################################
     * Change Detect Feature
     * 变化感知特性
     * ####################################################################
     */
    private HashMap<String, SharedProperty> propertyHashMap = new HashMap<>();
    private JsonObject changedJson = new JsonObject();
    private ArrayList<String> removes = new ArrayList<>();

    public boolean detectChange() {
        for (Map.Entry<String, SharedProperty> stringSharedPropertyEntry : propertyHashMap.entrySet()) {
            if (stringSharedPropertyEntry.getValue().detectChange()) return true;
        }

        return changedJson.size() != 0 || !removes.isEmpty();
    }

    public JsonObject generateAndClearUpdate() {

        JsonObject updated = changedJson;

        propertyHashMap.entrySet().forEach(j -> {
            if (j.getValue().detectChange()) {
                updated.add(j.getKey(), j.getValue().generateAndClearUpdate());
            }
        });

        if (!removes.isEmpty()) {
            JsonArray removeArray = new JsonArray();
            removes.forEach(j -> removeArray.add(j));

            updated.add("$remove", removeArray);
        }
        changedJson = new JsonObject();
        removes.clear();
        return updated;
    }

    public void synProperty(JsonObject jsonObject) {

        // syn json to now properties.
        jsonObject.entrySet().forEach(j -> {
            if (propertyHashMap.containsKey(j.getKey()))
                propertyHashMap.get(j.getKey()).synProperty(j.getValue().getAsJsonObject());
            else if (j.getValue().isJsonObject()) {
                SharedProperty property = new SharedProperty();
                property.completeJson = j.getValue().getAsJsonObject();
                propertyHashMap.put(j.getKey(), property);
            } else {
                completeJson.remove(j.getKey());
                JsonElement value = j.getValue();
                completeJson.add(j.getKey(), value);
            }
        });

        // remove all properties if has remove tag
        if (jsonObject.has("$remove")) {
            JsonArray array = jsonObject.getAsJsonArray("$remove");
            array.forEach(j -> {
                if (propertyHashMap.containsKey(j.getAsString())) propertyHashMap.remove(j.getAsString());
                if (completeJson.has(j.getAsString())) completeJson.remove(j.getAsString());
            });
        }
        clearShaderCache();
    }

    /**
     * ####################################################################
     * Basic CRUD
     * 基础增删改查
     * ####################################################################
     */
    protected JsonObject completeJson;

    public JsonObject getCompleteJson() {
        return completeJson;
    }

    public boolean hasProperty(String key) {
        return propertyHashMap.containsKey(key) || completeJson.has(key);
    }

    public void removeProperty(String key) {
        this.setProperty(key, null);
    }

    public void setProperty(String key, Object value) {
        if (value == null) {
            if (changedJson.has(key)) changedJson.remove(key);
            if (completeJson.has(key)) completeJson.remove(key);
            if (propertyHashMap.containsKey(key)) propertyHashMap.remove(key);
            if (!removes.contains(key)) removes.add(key);
        } else {
            if (value instanceof SharedProperty) {
                propertyHashMap.put(key, (SharedProperty) value);
                ((SharedProperty) value).changedJson = ((SharedProperty) value).completeJson;
                completeJson.add(key, ((SharedProperty) value).getCompleteJson());
            } else if (value instanceof JsonObject) {
                SharedProperty sharedProperty = new SharedProperty();
                sharedProperty.completeJson = (JsonObject) value;
                sharedProperty.changedJson = sharedProperty.completeJson;
                propertyHashMap.put(key, sharedProperty);
                completeJson.add(key, sharedProperty.getCompleteJson());
            } else if (value instanceof JsonElement) {
                completeJson.add(key, (JsonElement) value);
                changedJson.add(key, (JsonElement) value);
            } else if (value instanceof Number) {
                completeJson.addProperty(key, (Number) value);
                changedJson.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                completeJson.addProperty(key, (String) value);
                changedJson.addProperty(key, (String) value);
            } else if (value instanceof Boolean) {
                completeJson.addProperty(key, (Boolean) value);
                changedJson.addProperty(key, (Boolean) value);
            } else if (value instanceof Character) {
                completeJson.addProperty(key, (Character) value);
                changedJson.addProperty(key, (Character) value);
            }
        }
    }

    public JsonElement get(String key) {
        return completeJson.get(key);
    }


    public String getAsString(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsString();
        return "$empty";
    }

    public int getAsInt(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsInt();
        return -1;
    }

    public long getAsLong(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsLong();
        return -1l;
    }

    public double getAsDouble(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsDouble();
        return -1d;
    }

    public float getAsFloat(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsFloat();
        return -1f;
    }

    public boolean getAsBool(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsBoolean();
        return false;
    }

    public JsonArray getAsJsonArray(String key) {
        if (completeJson.has(key)) {
            return completeJson.getAsJsonArray(key);
        }
        return null;
    }

    /**
     * ####################################################################
     * Convenient Method Addition
     * 便捷功能
     * ####################################################################
     */

    public SharedProperty getAsProperty(String key) {
        if (propertyHashMap.containsKey(key)) {
            return propertyHashMap.get(key);
        } else if (hasProperty(key) && get(key).isJsonObject()) {
            SharedProperty sharedProperty = new SharedProperty(get(key).getAsJsonObject());
            this.setProperty(key, sharedProperty);
            return sharedProperty;
        }
        return EMPTY_PROPERTY;
    }

    public SharedProperty getOrCreateProperty(String key) {
        if (hasProperty(key)) return getAsProperty(key);
        SharedProperty property = new SharedProperty();
        setProperty(key, property);
        return property;
    }


    @Deprecated
    /**
     * 废弃方法，请勿使用。
     */ public SharedProperty getOrCreateSharedProperty(String key) {
        return getOrCreateProperty(key);
    }

    public JsonArray getArrayFromList(String key, List<?> value) {
        JsonArray array = new JsonArray();

        for (Object obj : value) {
            if (obj instanceof SharedProperty) {
                array.add(((SharedProperty) obj).completeJson);
            } else if (obj instanceof PropertyShader) {
                array.add(((PropertyShader) obj).property.completeJson);
            } else {
                array.add(String.valueOf(obj));
            }
        }

        return array;
    }

    @SneakyThrows
    public <T extends PropertyShader> List<T> getListFromArray(String key, Class<T> clazz) {

        Constructor<T> constructor = clazz.getConstructor(SharedProperty.class);

        List<T> list = new ArrayList<>();

        JsonElement element = getCompleteJson().get(key);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            array.forEach(j -> {
                JsonObject jsonObject = j.getAsJsonObject();

                try {
                    T t = constructor.newInstance(new SharedProperty(jsonObject));
                    list.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return list;
    }

    /**
     * ####################################################################
     * Decorate Feature
     * 装饰能力
     * ####################################################################
     */
    protected PropertyShader shader = null;

    public <T extends PropertyShader> T getAsShader(Class<T> shaderClazz) {
        try {
            if (this.shader == null || !this.shader.getClass().equals(shaderClazz)) {
                return (T) (this.shader = shaderClazz.getConstructor(SharedProperty.class).newInstance(this));
            }
            return (T) this.shader;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends PropertyShader> T getAsShader(String key, Class<T> shaderClazz) {
        if (hasProperty(key)) {
            return getAsProperty(key).getAsShader(shaderClazz);
        }
        return null;
    }

    public void clearShaderCache() {
        shader = null;
    }

}
