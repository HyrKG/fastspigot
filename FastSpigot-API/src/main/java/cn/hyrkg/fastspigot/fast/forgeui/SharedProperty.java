package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedProperty extends JsonProperty {

    public static SharedProperty EMPTY_PROPERTY = new SharedProperty();

    private HashMap<String, SharedProperty> propertyHashMap = new HashMap<>();

    // changes
    private JsonObject changedJson = new JsonObject();
    private ArrayList<String> removes = new ArrayList<>();

    public boolean detectChange() {
        for (Map.Entry<String, SharedProperty> stringSharedPropertyEntry : propertyHashMap.entrySet()) {
            if (stringSharedPropertyEntry.getValue().detectChange())
                return true;
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


    public void setProperty(String key, Object value) {
        if (value == null) {
            if (changedJson.has(key))
                changedJson.remove(key);
            if (completeJson.has(key))
                completeJson.remove(key);
            if (propertyHashMap.containsKey(key))
                propertyHashMap.remove(key);
            if (!removes.contains(key))
                removes.add(key);
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
                if (propertyHashMap.containsKey(j.getAsString()))
                    propertyHashMap.remove(j.getAsString());
                if (completeJson.has(j.getAsString()))
                    completeJson.remove(j.getAsString());
            });
        }
        clearShaderCache();
    }

    public void removeProperty(String key) {
        setProperty(key, null);
    }

    public SharedProperty getAsProperty(String key) {
        if (propertyHashMap.containsKey(key))
            return propertyHashMap.get(key);
        return EMPTY_PROPERTY;
    }

    public boolean hasProperty(String key) {
        return super.hasProperty(key) || propertyHashMap.containsKey(key);
    }

    @Override
    public JsonProperty getOrCreateProperty(String key) {
        if (hasProperty(key))
            return getAsProperty(key);
        SharedProperty property = new SharedProperty();
        setProperty(key, property);
        return property;
    }
}
