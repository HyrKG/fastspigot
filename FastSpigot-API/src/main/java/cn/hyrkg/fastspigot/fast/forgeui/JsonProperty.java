package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public void setProperty(String key, Object value) {
        if (value == null) {
            if (completeJson.has(key))
                completeJson.remove(key);
        } else {
            if (value instanceof SharedProperty) {
                setProperty(key, ((SharedProperty) value).completeJson);
            } else if (value instanceof JsonObject) {
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
            }
        }
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

    public SharedProperty getOrCreateSharedProperty(String key) {
        return null;
    }

}
