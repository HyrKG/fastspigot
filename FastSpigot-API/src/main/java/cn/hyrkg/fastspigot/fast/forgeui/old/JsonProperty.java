package cn.hyrkg.fastspigot.fast.forgeui.old;

import cn.hyrkg.fastspigot.fast.forgeui.PropertyShader;
import cn.hyrkg.fastspigot.fast.forgeui.SharedProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class JsonProperty {
    // raw obj
    protected JsonObject completeJson = new JsonObject();
    protected PropertyShader shader = null;

    public JsonProperty() {

    }

    public JsonProperty(JsonObject obj) {
        this.completeJson = obj;
    }

    public JsonObject getCompleteJson() {
        return completeJson;
    }

    public String getAsString(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsString();
        return "$empty";
    }

    public int getAsInt(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsInt();
        return -1;
    }

    public long getAsLong(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsLong();
        return -1l;
    }

    public double getAsDouble(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsDouble();
        return -1d;
    }

    public float getAsFloat(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsFloat();
        return -1f;
    }

    public boolean getAsBool(String key) {
        if (completeJson.has(key))
            return completeJson.get(key).getAsBoolean();
        return false;
    }

    public JsonArray getAsJsonArray(String key) {
        if (completeJson.has(key)) {
            return completeJson.getAsJsonArray(key);
        }
        return null;
    }

    /*
     * 设置内容
     * */
    public void setProperty(String key, Object value) {
        if (value == null) {
            if (completeJson.has(key))
                completeJson.remove(key);
        } else {
            if (value instanceof JsonProperty) {
                setProperty(key, ((JsonProperty) value).getCompleteJson());
            } else if (value instanceof PropertyShader)
                setProperty(key, ((PropertyShader) value).getProperty());
            else if (value instanceof JsonObject) {
                completeJson.add(key, (JsonObject) value);
            } else if (value instanceof JsonElement) {
                completeJson.add(key, (JsonElement) value);
            } else if (value instanceof Number) {
                completeJson.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                completeJson.addProperty(key, (String) value);
            } else if (value instanceof Boolean) {
                completeJson.addProperty(key, (Boolean) value);
            } else if (value instanceof Character) {
                completeJson.addProperty(key, (Character) value);
            } else if (value instanceof List) {
                JsonArray array = getArrayFromList(key, (List<?>) value);
                completeJson.add(key, array);
            }
        }
    }


    /**
     * 将List转换为JsonArray。
     * 仅支持JsonProperty或PropertyShader，其他一律被转换为String存入。
     */
    public JsonArray getArrayFromList(String key, List<?> value) {
        JsonArray array = new JsonArray();

        for (Object obj : value) {
            if (obj instanceof JsonProperty) {
                array.add(((JsonProperty) obj).completeJson);
            } else if (obj instanceof PropertyShader) {
                array.add(((PropertyShader) obj).property.getCompleteJson());
            } else {
                array.add(String.valueOf(obj));
            }
        }

        return array;
    }

    /*
     *  将JsonArray读出为PropertyShader
     * */
    @SneakyThrows
    public <T extends PropertyShader> List<T> getListFromArray(String key, Class<T> clazz) {

        Constructor<T> constructor = clazz.getConstructor(JsonProperty.class);

        List<T> list = new ArrayList<>();

        JsonElement element = getCompleteJson().get(key);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            array.forEach(j -> {
                JsonObject jsonObject = j.getAsJsonObject();

                try {
                    T t = constructor.newInstance(new JsonProperty(jsonObject));
                    list.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return list;
    }


    public boolean hasProperty(String key) {
        return completeJson.has(key);
    }

    public JsonProperty getAsProperty(String key) {
        if (hasProperty(key)) {
            JsonProperty jsonProperty = new JsonProperty();
            jsonProperty.completeJson = completeJson.get(key).getAsJsonObject();
            return jsonProperty;
        }
        return null;
    }

    public <T extends PropertyShader> T getAsShader(Class<T> shaderClazz) {
        try {
            if (this.shader == null || !this.shader.getClass().equals(shaderClazz)) {
                return (T) (this.shader = shaderClazz.getConstructor(JsonProperty.class).newInstance(this));
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

    public JsonProperty getOrCreateProperty(String key) {
        if (hasProperty(key))
            return getAsProperty(key);
        JsonProperty property = new JsonProperty();
        setProperty(key, property);
        return property;
    }

    @Deprecated
    /**
     * 废弃方法，请勿使用。
     */
    public SharedProperty getOrCreateSharedProperty(String key) {
        return getOrCreateSharedProperty(key);
    }

}
