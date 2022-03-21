package cn.hyrkg.fastspigot.fast.bungeebridge;

import com.google.gson.JsonObject;

public interface IBungeeRequestHandler {

    /**
     * 注意，在处理resultPacket时，请注意修改逻辑，该数据包将会具有传承性。
     *
     * @return 若返回不为空，将会把返回结果传入下一个包。若为null，则意味着中断并返回resultPacket作为结果。
     * */
    JsonObject handlerRequest(ServerInfo requestFrom, JsonObject originPacket,JsonObject resultPacket);
}
