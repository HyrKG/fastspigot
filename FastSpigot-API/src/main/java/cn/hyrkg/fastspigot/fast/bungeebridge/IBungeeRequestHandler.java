package cn.hyrkg.fastspigot.fast.bungeebridge;

import com.google.gson.JsonObject;

public interface IBungeeRequestHandler {
    JsonObject handlerRequest(ServerInfo requestFrom, JsonObject jsonObject);
}
