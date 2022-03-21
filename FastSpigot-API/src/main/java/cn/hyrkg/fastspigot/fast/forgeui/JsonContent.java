package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;

public class JsonContent {
    public final JsonProperty property;
    public final String key;

    protected boolean flagEmptyStringReturn = false;

    public JsonContent(JsonProperty property, String key) {
        this.property = property;
        this.key = key;
    }

    public boolean has() {
        return property.hasProperty(key);
    }

    public void set(Object value) {
        property.setProperty(key, value);
    }

    public String get() {
        if (!flagEmptyStringReturn) {
            return property.getAsString(key);
        } else {
            if (property.hasProperty(key))
                return property.getAsString(key);
            else
                return "";
        }
    }

    public int getInt() {
        return property.getAsInt(key);
    }

    public double getDouble() {
        return property.getAsDouble(key);
    }

    public float getFloat() {
        return property.getAsFloat(key);
    }

    public long getLong() {
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
