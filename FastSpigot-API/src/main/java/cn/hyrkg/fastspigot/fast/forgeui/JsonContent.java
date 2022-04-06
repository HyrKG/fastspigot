package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

public class JsonContent<T> {
    private final Class tClass;

    public final JsonProperty property;
    public final String key;

    protected boolean flagEmptyStringReturn = false;

    public JsonContent(JsonProperty property, String key) {
        this.property = property;
        this.key = key;

        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }

    public boolean has() {
        return property.hasProperty(key);
    }

    public void set(Object value) {
        property.setProperty(key, value);
    }


    public T get() {
        if (tClass.equals(String.class))
            return (T) getString();
        else if (tClass.equals(Integer.class))
            return (T) getInt();
        else if (tClass.equals(Double.class))
            return (T) getDouble();
        else if (tClass.equals(Float.class))
            return (T) getFloat();
        else if (tClass.equals(Long.class))
            return (T) getLong();
        else if (tClass.equals(JsonArray.class))
            return (T) getJsonArray();
        else if (tClass.equals(UUID.class)) {
            return ((T) UUID.fromString(getString()));
        } else if (tClass.asSubclass(PropertyShader.class)) {


        }
        return (T) null;
    }

    public String getString() {
        if (!flagEmptyStringReturn) {
            return property.getAsString(key);
        } else {
            if (property.hasProperty(key))
                return property.getAsString(key);
            else
                return "";
        }
    }

    public Integer getInt() {
        return property.getAsInt(key);
    }

    public Double getDouble() {
        return property.getAsDouble(key);
    }

    public Float getFloat() {
        return property.getAsFloat(key);
    }

    public Long getLong() {
        return property.getAsLong(key);
    }

    public JsonArray getJsonArray() {
        if (has())
            return property.getAsJsonArray(key);
        else
            return new JsonArray();
    }

    public JsonContent setFlagEmptyStringReturn(boolean flagEmptyStringReturn) {
        this.flagEmptyStringReturn = flagEmptyStringReturn;
        return this;
    }
}
