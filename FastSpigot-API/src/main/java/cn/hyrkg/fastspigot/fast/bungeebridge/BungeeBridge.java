package cn.hyrkg.fastspigot.fast.bungeebridge;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BungeeBridge {
    //该index将会被用于区分不同的订阅
    public final String index;

    /**
     * 注册请求处理器
     */
     public void addRequestHandler(IBungeeRequestHandler requestHandler)
     {

     }

     /*
     * 向BC发送请求
     * ！注意，该方法将会堵塞进程直至收到返回
     * */
     public JsonObject request(JsonObject jsonObject)
     {
       return null;
     }


}
