package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SimpleMsg {

    private final ForgeGuiHandler forgeGuiHandler;
    private final IForgeGui forgeGui;
    private final JsonObject jsonObj;

    private SimpleMsg(IForgeGui gui, ForgeGuiHandler guiHandler) {
        this.forgeGui = gui;
        this.forgeGuiHandler = guiHandler;
        jsonObj = new JsonObject();
    }

    public SimpleMsg add(String key, Object value) {
        if (value instanceof Number)
            jsonObj.addProperty(key, (Number) value);
        else if (value instanceof Character)
            jsonObj.addProperty(key, (Character) value);
        else if (value instanceof String)
            jsonObj.addProperty(key, (String) value);
        else if (value instanceof Boolean)
            jsonObj.addProperty(key, (Boolean) value);
        else if (value instanceof JsonElement)
            jsonObj.add(key, (JsonElement) value);
        return this;
    }

    public void sent() {
        forgeGuiHandler.sendMessage(this, forgeGui);
    }

    public JsonObject getJsonObj() {
        return jsonObj;
    }

    public static SimpleMsg create(IForgeGui gui, ForgeGuiHandler guiHandler) {
        return new SimpleMsg(gui, guiHandler);
    }
}
